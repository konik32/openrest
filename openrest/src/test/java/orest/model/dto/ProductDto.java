package orest.model.dto;

import java.util.List;

import lombok.Data;
import orest.dto.Dto;
import orest.dto.Dto.DtoType;
import orest.dto.Nullable;
import orest.model.Product;
import orest.model.User;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Dto(entityType = Product.class, name="productDto", type=DtoType.BOTH)
public class ProductDto {

	@JsonIgnore
	@Value("#{target.tempName}")
	private String name;

	private String description;

	@Nullable("userSet")
	private User user;
	
	private String tempName;
	
	private List<TagDto> tags;
	
	private boolean userSet = false;
	
	
	public void setUser(User user){
		this.user = user;
		this.userSet = true;
	}
}
