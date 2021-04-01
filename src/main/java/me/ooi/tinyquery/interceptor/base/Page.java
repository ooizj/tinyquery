package me.ooi.tinyquery.interceptor.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jun.zhao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
	
	private Integer pageNumber;
	private Integer pageSize;

}
