package Daos;

import Beans.Department;
import Beans.Employee;
import Beans.Job;
import Dtos.EmpleadosPorRegionDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author DTI
 */
public class EmployeeDao extends DaoBase {

    public ArrayList<Employee> listarEmpleados() {
        ArrayList<Employee> listaEmpleados = new ArrayList<>();

        try (Connection conn = this.getConection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees e \n"
                     + "left join jobs j on (j.job_id = e.job_id) \n"
                     + "left join departments d on (d.department_id = e.department_id)\n"
                     + "left  join employees m on (e.manager_id = m.employee_id)");) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt(1));
                employee.setFirstName(rs.getString(2));
                employee.setLastName(rs.getString(3));
                employee.setEmail(rs.getString(4));
                employee.setPhoneNumber(rs.getString(5));
                employee.setHireDate(rs.getString(6));
                Job job = new Job();
                job.setJobId(rs.getString(7));
                job.setJobTitle(rs.getString("job_title"));

                employee.setJob(job);
                employee.setSalary(rs.getBigDecimal(8));
                employee.setCommissionPct(rs.getBigDecimal(9));

                Employee manager = new Employee();
                manager.setEmployeeId(rs.getInt("e.manager_id"));
                manager.setFirstName(rs.getString("m.first_name"));
                manager.setLastName(rs.getString("m.last_name"));

                employee.setManager(manager);

                Department department = new Department();
                department.setDepartmentId(rs.getInt(11));
                department.setDepartmentName(rs.getString("d.department_name"));
                employee.setDepartment(department);

                listaEmpleados.add(employee);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listaEmpleados;
    }

    public Employee obtenerEmpleado(int employeeId) {

        Employee employee = null;

        String sql = "SELECT * FROM employees e WHERE employee_id = ?";

        try (Connection conn = this.getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql);) {

            pstmt.setInt(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery();) {

                if (rs.next()) {
                    employee = new Employee();
                    employee.setEmployeeId(rs.getInt(1));
                    employee.setFirstName(rs.getString(2));
                    employee.setLastName(rs.getString(3));
                    employee.setEmail(rs.getString(4));
                    employee.setPhoneNumber(rs.getString(5));
                    employee.setHireDate(rs.getString(6));

                    Job job = new Job();
                    job.setJobId(rs.getString(7));
                    employee.setJob(job);

                    employee.setSalary(rs.getBigDecimal(8));
                    employee.setCommissionPct(rs.getBigDecimal(9));

                    Employee manager = new Employee();
                    manager.setEmployeeId(rs.getInt(10));
                    employee.setManager(manager);

                    Department department = new Department();
                    department.setDepartmentId(rs.getInt(11));
                    employee.setDepartment(department);

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return employee;
    }

    public void guardarEmpleado(Employee employee) {

        String sql = "INSERT INTO employees (first_name, last_name, email, phone_number, hire_date, job_id, salary, commission_pct, manager_id, department_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql);) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPhoneNumber());
            pstmt.setString(5, employee.getHireDate());
            pstmt.setString(6, employee.getJob().getJobId());
            pstmt.setBigDecimal(7, employee.getSalary());
            if (employee.getCommissionPct() == null) {
                pstmt.setNull(8, Types.DECIMAL);
            } else {
                pstmt.setBigDecimal(8, employee.getCommissionPct());
            }
            pstmt.setInt(9, employee.getManager().getEmployeeId());
            pstmt.setInt(10, employee.getDepartment().getDepartmentId());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actualizarEmpleado(Employee employee) {

        String sql = "UPDATE employees "
                + "SET first_name = ?, "
                + "last_name = ?, "
                + "email = ?, "
                + "phone_number = ?, "
                + "hire_date = ?, "
                + "job_id = ?, "
                + "salary = ?, "
                + "commission_pct = ?, "
                + "manager_id = ?, "
                + "department_id = ? "
                + "WHERE employee_id = ?";

        try (Connection conn = this.getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql);) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPhoneNumber());
            pstmt.setString(5, employee.getHireDate());
            pstmt.setString(6, employee.getJob().getJobId());
            pstmt.setBigDecimal(7, employee.getSalary());
            if (employee.getCommissionPct() == null) {
                pstmt.setNull(8, Types.DECIMAL);
            } else {
                pstmt.setBigDecimal(8, employee.getCommissionPct());
            }
            pstmt.setInt(9, employee.getManager().getEmployeeId());
            pstmt.setInt(10, employee.getDepartment().getDepartmentId());
            pstmt.setInt(11, employee.getEmployeeId());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void borrarEmpleado(int employeeId) {

        try (Connection conn = this.getConection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM employees WHERE employee_id = ?");) {

            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public Employee validarUsuarioPasswordHash(String username, String password) {

        Employee employee = new Employee();
        String sql = "select * from employees_credentials where email=? and password_hashed =sha2(?,256);";
        try (Connection conn = this.getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeQuery();

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    employee.setEmployeeId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employee;
    }

    public ArrayList<EmpleadosPorRegionDto> listaEmpleadosPorRegion() {
        ArrayList<EmpleadosPorRegionDto> lista = new ArrayList<>();

        String sql = "SELECT r.region_name, count(c.country_id) \n" +
                "FROM employees e\n" +
                "INNER JOIN departments d ON e.department_id = d.department_id\n" +
                "INNER JOIN locations l ON l.location_id = d.location_id\n" +
                "INNER JOIN countries c ON c.country_id = l.country_id\n" +
                "INNER JOIN regions r ON c.region_id = r.region_id\n" +
                "GROUP BY r.region_id;";

        try (Connection conn = getConection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EmpleadosPorRegionDto employee = new EmpleadosPorRegionDto();
                employee.setNombreRegion(rs.getString(1));
                employee.setCantidadEmpleados(rs.getInt(2));
                lista.add(employee);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lista;
    }
}
