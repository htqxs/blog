package fit.xiaozhang.blog.service.impl;


import fit.xiaozhang.blog.dto.UniqueViewDTO;
import fit.xiaozhang.blog.entity.UniqueView;
import fit.xiaozhang.blog.dao.UniqueViewDao;
import fit.xiaozhang.blog.service.UniqueViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fit.xiaozhang.blog.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangzhi
 * @since 2020-05-18
 */
@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewDao, UniqueView> implements UniqueViewService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UniqueViewDao uniqueViewDao;

    // 每天 0 点执行一次
    @Scheduled(cron = " 0 0 0 * * ?")
    @Override
    public void saveUniqueView() {
        // 获取每天用户量
        Long count = redisTemplate.boundSetOps("ip_set").size();
        // 获取昨天日期插入数据
        UniqueView uniqueView = UniqueView.builder()
                .createTime(DateUtil.getSomeDay(new Date(), -1))
                .viewsCount(Objects.nonNull(count) ? count.intValue() : 0).build();
        uniqueViewDao.insert(uniqueView);
    }

    @Override
    public List<UniqueViewDTO> listUniqueViews() {
        String startTime = DateUtil.getMinTime(DateUtil.getSomeDay(new Date(), -7));
        String endTime = DateUtil.getMaxTime(new Date());
        return uniqueViewDao.listUniqueViews(startTime, endTime);
    }

}
