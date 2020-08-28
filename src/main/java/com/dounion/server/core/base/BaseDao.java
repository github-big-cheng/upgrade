package com.dounion.server.core.base;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("baseDao")
public abstract class BaseDao<T extends BaseEntity> {

    @Autowired
    protected SqlSessionTemplate sql;

    public void setSql(SqlSessionTemplate sql) {
        this.sql = sql;
    }

    public String getNamespace(){
        return "";
    };

    public int deleteByPrimaryKey(Integer id) {
        return sql.delete(this.getNamespace() + ".deleteByPrimaryKey", id);
    }

    public int insert(T record) {
        return sql.insert(this.getNamespace() + ".insert", record);
    }

    public int insertSelective(T record) {
        return sql.insert(this.getNamespace() + ".insert", record);
    }

    public T selectByPrimaryKey(Integer id) {
        return sql.selectOne(this.getNamespace() + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(T record) {
        return sql.update(this.getNamespace() + ".updateByPrimaryKeySelective", record);
    }

    public int updateByPrimaryKey(T record) {
        return sql.update(this.getNamespace() + ".updateByPrimaryKey", record);
    }

    public List<T> selectListBySelective(T query) {
        return sql.selectList(this.getNamespace() + ".selectListBySelective", query);
    }
}
