package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigTagsRelationMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.ArrayList;
import java.util.List;

/**
 *  The mysql implementation of ConfigInfoMapper.
 *
 *  @author fuhouyu
 */
public class ConfigTagsRelationMapperByPostgres extends AbstractMapperByPostgres implements ConfigTagsRelationMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public MapperResult findConfigInfo4PageFetchRows(MapperContext context) {
        String tenant = (String) context.getWhereParameter("tenantId");
        String dataId = (String) context.getWhereParameter("dataId");
        String group = (String) context.getWhereParameter("groupId");
        String appName = (String) context.getWhereParameter("app_name");
        String content = (String) context.getWhereParameter("content");
        String[] tagArr = (String[]) context.getWhereParameter("tagARR");
        List<Object> paramList = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" a.tenant_id=? ");
        paramList.add(tenant);
        if (StringUtils.isNotBlank(dataId)) {
            where.append(" AND a.data_id=? ");
            paramList.add(dataId);
        }

        if (StringUtils.isNotBlank(group)) {
            where.append(" AND a.group_id=? ");
            paramList.add(group);
        }

        if (StringUtils.isNotBlank(appName)) {
            where.append(" AND a.app_name=? ");
            paramList.add(appName);
        }

        if (!StringUtils.isBlank(content)) {
            where.append(" AND a.content LIKE ? ");
            paramList.add(content);
        }

        where.append(" AND b.tag_name IN (");

        for (int i = 0; i < tagArr.length; ++i) {
            if (i != 0) {
                where.append(", ");
            }

            where.append('?');
            paramList.add(tagArr[i]);
        }

        where.append(") ");
        return new MapperResult("SELECT a.id,a.data_id,a.group_id,a.tenant_id,a.app_name,a.content FROM config_info  a LEFT JOIN config_tags_relation b ON a.id=b.id" + where + " OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize(), paramList);
    }

    @Override
    public MapperResult findConfigInfoLike4PageFetchRows(MapperContext context) {
        String tenant = (String) context.getWhereParameter("tenantId");
        String dataId = (String) context.getWhereParameter("dataId");
        String group = (String) context.getWhereParameter("groupId");
        String appName = (String) context.getWhereParameter("app_name");
        String content = (String) context.getWhereParameter("content");
        String[] tagArr = (String[]) ((String[]) context.getWhereParameter("tagARR"));
        List<Object> paramList = new ArrayList();
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" a.tenant_id LIKE ? ");
        paramList.add(tenant);
        if (!StringUtils.isBlank(dataId)) {
            where.append(" AND a.data_id LIKE ? ");
            paramList.add(dataId);
        }

        if (!StringUtils.isBlank(group)) {
            where.append(" AND a.group_id LIKE ? ");
            paramList.add(group);
        }

        if (!StringUtils.isBlank(appName)) {
            where.append(" AND a.app_name = ? ");
            paramList.add(appName);
        }

        if (!StringUtils.isBlank(content)) {
            where.append(" AND a.content LIKE ? ");
            paramList.add(content);
        }

        where.append(" AND b.tag_name IN (");

        for (int i = 0; i < tagArr.length; ++i) {
            if (i != 0) {
                where.append(", ");
            }

            where.append('?');
            paramList.add(tagArr[i]);
        }

        where.append(") ");
        return new MapperResult("SELECT a.id,a.data_id,a.group_id,a.tenant_id,a.app_name,a.content FROM config_info a LEFT JOIN config_tags_relation b ON a.id=b.id " + where + " OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize(), paramList);
    }
}
