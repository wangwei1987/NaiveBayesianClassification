package com.zxxk;

import com.zxxk.dao.KPointDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei.
 */
@Service
public class ToolsService {

    @Resource
    private KPointDao kPointDao;

    @Transactional
    public void saveKPointCount(List<Map<String, Object>> saveList) {
        kPointDao.saveKPointCount(saveList);
    }
}
