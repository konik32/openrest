package pl.openrest.dto.dummy.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {

    private String name;

    private String description;

    private Integer productionYear;

    private User user;

    private List<Tag> tags;
}
