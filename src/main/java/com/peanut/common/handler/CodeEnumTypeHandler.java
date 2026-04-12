package com.peanut.common.handler;

import com.peanut.enumPackage.CodeDescEnum;
import com.peanut.utils.CodeEnumUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: peanut
 * @date: 2026/3/30
 * @version:1.0
 */
public class CodeEnumTypeHandler<E extends Enum<E> & CodeDescEnum<E>> extends BaseTypeHandler<E> {
    private Class<E> type;

    public CodeEnumTypeHandler(Class<E> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    public CodeEnumTypeHandler() {
        // 必须有无参构造！！！
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDesc());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return rs.wasNull() ? null : CodeEnumUtil.codeOf(type, code);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return rs.wasNull() ? null : CodeEnumUtil.codeOf(type, code);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return cs.wasNull() ? null : CodeEnumUtil.codeOf(type, code);
    }


}