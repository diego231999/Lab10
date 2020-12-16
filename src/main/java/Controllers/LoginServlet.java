package Controllers;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import Beans.Employee;
import Daos.DepartmentDao;
import Daos.EmployeeDao;
import Daos.JobDao;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action") == null ? "login" : request.getParameter("action");
        Employee employee;
        EmployeeDao ed = new EmployeeDao();
        JobDao jd=new JobDao();
        String correo = request.getParameter("inputEmail");
        String pass = request.getParameter("inputPassword");

        employee = ed.validarUsuarioPasswordHash(correo, pass);
        System.out.println(employee.getEmployeeId());

        if (employee.getEmployeeId() != 0) {
            int max_salary=jd.obtenerMaxSalary(employee.getEmployeeId());
            System.out.println(max_salary);
            HttpSession session = request.getSession();

            if(max_salary>15000){
                session.setAttribute("employeeSession", employee);
                session.setAttribute("rol","Top1");
                response.sendRedirect(request.getContextPath() + "/EmployeeServlet");
            }else if(max_salary>8500 && max_salary<=15000){
                session.setAttribute("employeeSession", employee);
                session.setAttribute("rol","Top2");
                response.sendRedirect(request.getContextPath() + "/JobServlet");
            }else if(max_salary>5000 && max_salary<=8500){
                session.setAttribute("employeeSession", employee);
                session.setAttribute("rol","Top3");
                response.sendRedirect(request.getContextPath() + "/DepartmentServlet");
            }else if (max_salary<=5000){
                session.setAttribute("employeeSession", employee);
                session.setAttribute("rol","Top4");
                response.sendRedirect(request.getContextPath() + "/CountryServlet");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");

        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "login" : request.getParameter("action");


        switch (action) {
            case "logout":
                /*
                Inserte su código aquí
                 */
                break;
        }
    }
}
