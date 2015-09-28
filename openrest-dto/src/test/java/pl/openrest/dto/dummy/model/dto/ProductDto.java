package pl.openrest.dto.dummy.model.dto;

import java.util.List;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.annotations.Nullable;
import pl.openrest.dto.dummy.model.Product;
import pl.openrest.dto.registry.DtoType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Dto(entityType = Product.class, name = "productDto", type = DtoType.BOTH)
public class ProductDto {

	@JsonIgnore
	@Value("#{target.tempName}")
	private String name;

	private String description;

	@Nullable("userSet")
	private UserDto user;

	private String tempName;

	private List<TagDto> tags;

	private boolean userSet = false;

	public void setUser(UserDto user) {
		this.user = user;
		this.userSet = true;
	}
}
