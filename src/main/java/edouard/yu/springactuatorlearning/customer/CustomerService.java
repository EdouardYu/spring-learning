package edouard.yu.springactuatorlearning.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {
    private final JdbcTemplate jdbcTemplate;

    public CustomerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Customer> customerRowMapper = (
        (rs, rowNum) -> new Customer(
            rs.getInt("id"),
            rs.getString("email")
        )
    );

    public List<Customer> getAll() {
        String GET_ALL = "SELECT * FROM customer";
        return jdbcTemplate.query(GET_ALL, this.customerRowMapper);
    }
}
