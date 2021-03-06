package Daos;

import Beans.Department;
import Beans.Job;
import Beans.JobHistory;
import Dtos.EmpleadosPorRegionDto;
import Dtos.JobHistoryDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stuardo
 */
public class JobHistoryDao extends DaoBase {

    public ArrayList<JobHistoryDto> listarJobHistories(int employeeId) {

        ArrayList<JobHistoryDto> lista = new ArrayList<>();


        String sql = "select jh.start_date, jh.end_date, j.job_title, d.department_name \n" +
                "from job_history jh\n" +
                "INNER JOIN departments d ON jh.department_id  = d.department_id\n" +
                "INNER JOIN jobs j ON jh.job_id = j.job_id\n" +
                "where jh.employee_id=? \n" +
                "ORDER BY jh.start_date ASC;";

        try (Connection conn = getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    JobHistoryDto jobHistory = new JobHistoryDto();
                    jobHistory.setStartDate(rs.getString(1));
                    jobHistory.setEndDate(rs.getString(2));
                    jobHistory.setJobTitle(rs.getString(3));
                    jobHistory.setDepartment(rs.getString(4));
                    lista.add(jobHistory);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lista;
    }

    public JobHistory obtenerUltimoJobHistory(int employeeId) {
        JobHistory jobHistory = null;
        try {
            String sql = "SELECT * FROM job_history WHERE employee_id = ? order by end_date desc limit 0,1";
            try (Connection conn = this.getConection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);) {
                pstmt.setInt(1, employeeId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        jobHistory = new JobHistory();
                        jobHistory.setEmployeeId(rs.getInt(1));
                        jobHistory.setStartDate(rs.getString(2));
                        jobHistory.setEndDate(rs.getString(3));
                        Job job = new Job();
                        job.setJobId(rs.getString(4));
                        jobHistory.setJob(job);
                        Department department = new Department();
                        department.setDepartmentId(rs.getInt(5));
                        jobHistory.setDepartment(department);
                    }
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jobHistory;
    }

    public void CrearJobHistory(int employeeId, String startDate, String jobId, int departmentId) {

        try (Connection conn = this.getConection();) {
            String sql = "INSERT INTO job_history (`employee_id`, `start_date`, `end_date`, `job_id`, `department_id`) "
                    + "VALUES (?,?,now(),?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employeeId);
                pstmt.setString(2, startDate);
                pstmt.setString(3, jobId);
                pstmt.setInt(4, departmentId);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

