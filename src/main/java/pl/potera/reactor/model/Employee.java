package pl.potera.reactor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
public class Employee {
    @Id
    private String id;

    private String name;

    private BigDecimal points;
}
