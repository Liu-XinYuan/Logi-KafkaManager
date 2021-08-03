package com.xiaojukeji.kafka.manager.web.api.versionone.brooklin;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.brooklin.BrooklinClusterDTO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.BrooklinClusterService;
import com.xiaojukeji.kafka.manager.web.converters.BrooklinClusterModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Brooklin-Cluster集群管理相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_BR_PREFIX)
public class BrooklinClusterController {

    @Autowired
    private BrooklinClusterService clusterService;

    @ApiOperation(value = "接入集群")
    @PostMapping(value = "clusters")
    @ResponseBody
    public Result addNew(@RequestBody BrooklinClusterDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                clusterService.addNew(BrooklinClusterModelConverter.convert2ClusterDO(dto), SpringTool.getUserName())
        );
    }

    @ApiOperation(value = "列出所有集群")
    @GetMapping(value = "clusters")
    @ResponseBody
    public Result listAll() {
        return Result.buildFrom(ResultStatus.SUCCESS, clusterService.listAll());
    }

    @ApiOperation(value = "删除集群")
    @DeleteMapping(value = "clusters")
    @ResponseBody
    public Result delete(@RequestParam(value = "clusterId") Long clusterId) {
        return Result.buildFrom(clusterService.deleteById(clusterId, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改集群信息")
    @PutMapping(value = "clusters")
    @ResponseBody
    public Result modify(@RequestBody BrooklinClusterDTO reqObj) {
        if (ValidateUtils.isNull(reqObj) || !reqObj.legal() || ValidateUtils.isNull(reqObj.getClusterId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                clusterService.updateById(BrooklinClusterModelConverter.convert2ClusterDO(reqObj), SpringTool.getUserName())
        );
    }

    @ApiOperation(value = "开启|关闭集群监控")
    @PutMapping(value = "clusters/{clusterId}/monitor")
    @ResponseBody
    public Result modifyStatus(@PathVariable Long clusterId, @RequestParam("status") Integer status) {
        return Result.buildFrom(
                clusterService.modifyStatus(clusterId, status, SpringTool.getUserName())
        );
    }

}
