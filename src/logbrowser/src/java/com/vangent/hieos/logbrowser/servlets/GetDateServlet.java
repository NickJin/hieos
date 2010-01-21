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
package com.vangent.hieos.logbrowser.servlets;

import com.vangent.hieos.logbrowser.log.db.Log;
import com.vangent.hieos.logbrowser.log.db.LoggerException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class GetDateServlet extends HttpServlet {

    private final static Logger logger = Logger.getLogger(GetDateServlet.class);
    private String sqlCommand = null;

    /**
     *
     * @param config
     * @throws javax.servlet.ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        sqlCommand = config.getInitParameter("sqlCommand");
    }

    /**
     *
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        PreparedStatement dateStatement = null;
        Log log = new Log();
        try {
            Connection con = log.getConnection();
            System.out.println("LOG (dateStatement) = " + this.sqlCommand);
            dateStatement = con.prepareStatement(this.sqlCommand);
        } catch (SQLException e) {
            logger.error(e);
        } catch (LoggerException e) {
            logger.error(e);
        }
        if (dateStatement == null) {
            try {
                log.closeConnection();
                return; // EARLY EXIT: Can not continue processing
            } catch (LoggerException ex) {
                logger.error(ex);
            }
        }
        ResultSet result = null;
        try {
            result = dateStatement.executeQuery();
            TreeSet<Date> set = new TreeSet<Date>();
            SimpleDateFormat sdf = null;
            SimpleDateFormat sdf2 = null;
            if (req.getParameter("formatDisplayed") == null) {
                sdf = new SimpleDateFormat("yyyy-MMM dd");
            } else {
                sdf = new SimpleDateFormat(req.getParameter("formatDisplayed"));
            }
            if (req.getParameter("formatValue") == null) {
                sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            } else {
                sdf2 = new SimpleDateFormat(req.getParameter("formatValue"));
            }
            Timestamp timeStamp = null;
            String stringTmp = null;
            Date d = null;
            while (result.next()) {
                timeStamp = result.getTimestamp(1);
                stringTmp = sdf.format(timeStamp);
                d = sdf.parse(stringTmp);
                set.add(d);
            }
            Object[] stArray = set.toArray();
            StringBuffer time = new StringBuffer();
            res.setContentType("text/xml");
            time.append("<select>");
            time.append("<option value ='0' ></option>\n");
            for (int i = stArray.length - 1; i > -1; i--) {
                time.append("<option value ='" + sdf2.format((Date) stArray[i]) + "' > " + sdf.format((Date) stArray[i]) + "</option>\n");
            }
            time.append("</select >");
            stArray = null;
            res.getWriter().write(time.toString());
            time = null;
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(result != null){
                    result.close();
                }
                if (dateStatement != null) {
                    dateStatement.close();
                }
                log.closeConnection();
            } catch (LoggerException ex) {
                logger.error(ex);
            } catch (SQLException se) {
                logger.error(se);
            }
        }
    }
}
