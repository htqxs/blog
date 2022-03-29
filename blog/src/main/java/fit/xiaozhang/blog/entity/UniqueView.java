package fit.xiaozhang.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 网站访问量
 *
 * @author zhangzhi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_unique_view")
public class UniqueView {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 访问量
     */
    private Integer viewsCount;
}
