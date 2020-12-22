package me.ooi.tinyquery.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
	
	private Integer pageNumber;
	private Integer pageSize;

}
