package edouard.yu.springactuatorlearning.kpi;

import edouard.yu.springactuatorlearning.customer.CustomerService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "customers")
public class CustomerKPI {
    // on crée notre propre key performance indicator (KPI) dans actuator
    private final CustomerService customerService;

    public CustomerKPI(CustomerService customerService) {
        this.customerService = customerService;
    }

    @ReadOperation
    public CustomerCount count() {
        return new CustomerCount(this.customerService.getAll().size());
    }
}
