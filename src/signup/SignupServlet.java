package signup;

import java.io.*;
import java.nio.file.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private static final String USERS_DIR = "users";

    @Override
    public void init() throws ServletException {
        // Create users directory if it doesn't exist
        String realPath = getServletContext().getRealPath("/");
        File usersDir = new File(realPath, USERS_DIR);
        if (!usersDir.exists()) {
            usersDir.mkdirs();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Basic validation
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            response.sendRedirect("index.html?error=empty");
            return;
        }

        if (!password.equals(confirmPassword)) {
            response.sendRedirect("index.html?error=mismatch");
            return;
        }

        try {
            // Get the real path of the web application
            String realPath = getServletContext().getRealPath("/");
            File usersDir = new File(realPath, USERS_DIR);

            // Create user file
            String fileName = username + ".txt";
            File userFile = new File(usersDir, fileName);

            // Check if user already exists
            if (userFile.exists()) {
                response.sendRedirect("index.html?error=exists");
                return;
            }

            // Save user credentials
            try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
                writer.println("Username: " + username);
                writer.println("Password: " + password); // In production, hash the password!
                writer.println("Created: " + new java.util.Date());
            }

            // Redirect to success page
            response.sendRedirect("success.html?username=" + username);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("index.html?error=server");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the signup page
        response.sendRedirect("index.html");
    }
}
