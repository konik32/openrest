package orest.model.dto;

import java.util.List;

import lombok.Data;
import orest.dto.Dto;
import orest.model.Product;
import orest.model.User;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Dto(entityType = Product.class, name="productDto")
public class ProductDto {

	@JsonIgnore
	@Value("#{target.tempName}")
	private String name;

	private String description;

	private User user;
	
	private String tempName;
	
	private List<TagDto> tags;
}
