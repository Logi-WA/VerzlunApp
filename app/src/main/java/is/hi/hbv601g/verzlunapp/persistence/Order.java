package is.hi.hbv601g.verzlunapp.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private Long id;
    private User user;
    private Date orderDate;
    private String status;
    private List<OrderItem> orderItems = new ArrayList<>();
}
