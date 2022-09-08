package com.oushu.service.impl;

import com.google.gson.JsonObject;
import com.oushu.phoenix.jdbc.PhoenixQuery;
import com.oushu.service.SqlService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SqlServiceImpl implements SqlService {

    private PhoenixQuery pq = new PhoenixQuery();

    /**
     * @param sql
     * @return
     */
    @Override
    public boolean execSQL(String sql) {
        return pq.execute(sql, new HashMap<>()) == 0;
    }
}
