package fit.xiaozhang.blog.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import fit.xiaozhang.blog.annotation.OptLog;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.BlogBackInfoDTO;
import fit.xiaozhang.blog.dto.BlogHomeInfoDTO;
import fit.xiaozhang.blog.service.BlogInfoService;
import fit.xiaozhang.blog.vo.Result;

import static fit.xiaozhang.blog.constant.OptTypeConst.UPDATE;

/**
 * @author zhangzhi
 */
@Api(tags = "博客信息模块")
@RestController
public class BlogInfoController {
    @Autowired
    private BlogInfoService blogInfoService;

    @ApiOperation(value = "查看博客信息")
    @GetMapping("/")
    public Result<BlogHomeInfoDTO> getBlogHomeInfo() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getBlogInfo());
    }

    @ApiOperation(value = "查看后台信息")
    @GetMapping("/admin")
    public Result<BlogBackInfoDTO> getBlogBackInfo() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getBlogBackInfo());
    }

    @ApiOperation(value = "查看关于我信息")
    @GetMapping("/about")
    public Result<String> getAbout() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getAbout());
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改关于我信息")
    @PutMapping("/admin/about")
    public Result updateAbout(String aboutContent) {
        blogInfoService.updateAbout(aboutContent);
        return new Result<>(true, StatusConst.OK, "修改成功");
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改公告")
    @PutMapping("/admin/notice")
    public Result updateNotice(String notice) {
        blogInfoService.updateNotice(notice);
        return new Result<>(true, StatusConst.OK, "修改成功");
    }

    @ApiOperation(value = "查看公告")
    @GetMapping("/admin/notice")
    public Result<String> getNotice() {
        return new Result<>(true, StatusConst.OK, "查看成功", blogInfoService.getNotice());
    }
}

