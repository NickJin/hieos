/*
 * This code is subject to the HIEOS License, Version 1.0
 *
 * Copyright(c) 2008-2009 Vangent, Inc.  All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vangent.hieos.logbrowser.log.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * Class allowing to do several tasks on a database such as : <br/>
 * <ul>
 *   <li>create a table</li>
 *   <li>List fields in a table</li>
 *   <li>Test if a table exists</li>
 *   <li>Test if a message exists</li>
 * </ul>
 * <br />
 * An abstract log table contains a String tableName, a message identifier which allows to 
 * test if a message exists in a table, the createTableSqlCommand, readSqlCommand and writeSqlCommand 
 * have to be defined later and will be the command used to read or write in the database.
 * 
 * @author jbmeyer
 * @author Bernie Thuman (BHT) - streamlined, added more comments, removed "on-the-fly" table creation.
 *
 */
public abstract class AbstractLogTable {

    protected Connection conn;
    protected String messageId;
    protected String tableName;
    private final static Logger logger = Logger.getLogger(AbstractLogTable.class);

    /**
     * Tests if the message 'messageID' exists in the table named 'tableName'
     * @param messageID
     * @param tableName
     * @return
     * @throws SQLException
     */
    public boolean messageExist(String messageID, String tableName) throws LoggerException {
        String currentTable = tableName.toLowerCase();
        try {
            if (conn == null || conn.isClosed()) {
                throw new LoggerException("Database null or closed");
            }
        } catch (SQLException e) {
            throw new LoggerException("Database null or closed");
        }

        String sqlRequest = "SELECT count(*) from " + currentTable + " where  messageid='" + messageID + "'";
        if (logger.isDebugEnabled()){
            logger.debug("MessageExist SQL: " + sqlRequest);
        }
        Statement st = null;
        ResultSet res = null;
        try {
            st = conn.createStatement();
            res = st.executeQuery(sqlRequest);
            res.next();
            if (res.getInt(1) == 0) {
                return false;
            } else if (res.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("AbstractLogTable::messageExist()::" + e.getMessage());
            throw new LoggerException("AbstractLogTable::messageExist()::" + e.getMessage());
        }finally {
            try{
                if (res != null){
                    res.close();
                }
                if (st != null){
                    st.close();
                }
            }catch (SQLException se) {
                logger.error("Unable to close ResultSet and/or Statement: " + se);
            }
        }

        return false;
    }

    /**
     * 
     * @param inMessageId
     * @param deletePreparedStatement
     */
    public void deleteMessage(String inMessageId, PreparedStatement deletePreparedStatement) {
        try {
            deletePreparedStatement.setString(1, inMessageId);
            logger.info("Deleting log data for Message ID: " + inMessageId);
            deletePreparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Unable to delete data for Message ID :" + inMessageId);
            e.printStackTrace();
        }
    }
}
