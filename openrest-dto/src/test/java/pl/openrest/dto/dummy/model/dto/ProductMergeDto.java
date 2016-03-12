package pl.openrest.dto.dummy.model.dto;

import lombok.Data;
import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.dummy.model.Product;
import pl.openrest.dto.dummy.model.User;
import pl.openrest.dto.registry.DtoType;

@Data
@Dto(entityType = Product.class, name = "productMergeDto", type = DtoType.MERGE)
public class ProductMergeDto {

	private User user;
}
