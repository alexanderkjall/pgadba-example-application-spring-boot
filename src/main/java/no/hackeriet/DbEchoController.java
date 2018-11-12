package no.hackeriet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbEchoController {
    private final Connection connection;

    public DbEchoController() {
        String url = "jdbc:postgresql://localhost/test";
        Properties props = new Properties();
        props.setProperty("user","test");
        props.setProperty("password","test");
        props.setProperty("ssl","false");
        try {
            connection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/{val}")
    public String index(@PathVariable("val") String val) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select ? as t");
        ps.setString(1, val);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return rs.getString("t");
        } else {
            throw new RuntimeException("error");
        }
    }
}
