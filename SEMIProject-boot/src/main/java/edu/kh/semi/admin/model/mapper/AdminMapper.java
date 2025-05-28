package edu.kh.semi.admin.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

	int memberDelete(int memberNo);
	

}
