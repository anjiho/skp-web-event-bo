package kr.co.syrup.adreport.framework.dao;

import com.google.common.base.Joiner;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by ho on 2017. 2. 16..
 */
public class ListParameterTypeHandler implements TypeHandler{
    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
//        List<?> arrParam = (List<?>) parameter;
//        String inString = "";
//        for(Object element : arrParam){
//            inString = "," + String.valueOf(element);
//        }
//        inString = inString.substring(1);
//        ps.setString(i,inString);
        ps.setString(i, Joiner.on(",").join((Collection) parameter));
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        return null;
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }


}
