package org.springframework.data.rest.webmvc.config;

import lombok.Data;

import org.springframework.data.rest.webmvc.PersistentEntityResource;

@Data
public class PersistentEntityResourceWithDtoWrapper {
	private final PersistentEntityResource entityResource;
	private final Object dto;
}
