package org.ibs.cds.gode.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedData<T> implements Serializable {

	private List<T> data;
	private QueryContext context;

	@JsonIgnore
	public Stream<T> stream(){
		return CollectionUtils.isEmpty(data) ?  Stream.empty() : data.stream();
	}
}
