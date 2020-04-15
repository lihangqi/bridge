<template>
  <section>
    <!--menu-->
    <el-row style="margin-top: 15px">
      <el-menu :default-active="activeIndex" mode="horizontal" @select="doWhenSelectChanged" style="width: 100%">
        <el-submenu index="0">
          <template slot="title">
            <span style="font-weight: bold;font-size: 18px;">{{menuName}} /</span>
            <span v-if="envId === '0'" style="font-weight: bold;font-size: 26px">{{env}}</span>
            <span v-if="envId === '1'" style="font-weight: bold;font-size: 26px">{{env}}</span>
            <span v-if="envId === '2'" style="font-weight: bold;font-size: 26px;color: #E6A23C">{{env}}</span>
            <span v-if="envId === '3'" style="font-weight: bold;font-size: 26px;color: #F56C6C">{{env}}</span>
          </template>
          <div v-for="(team,index) in selectorList" :key="index">
            <el-submenu :index="team.teamId + ''" v-if="team.configAppList.length > 0" style="font-weight: 500">
              <template slot="title">{{team.teamName}}</template>
              <el-menu-item v-if="team.configAppList.length > 0"
                            v-for="(app,appIndex) in team.configAppList"
                            :index="team.teamId + '-' + app.appId" :key="appIndex" style="font-weight: 500">
                {{app.appName}}
              </el-menu-item>
            </el-submenu>
            <el-menu-item disabled :index="team.teamId + ''" v-else>{{team.teamName}}</el-menu-item>
          </div>
        </el-submenu>
      </el-menu>
    </el-row>

    <!--    <el-row style="padding-top: 15px;padding-left: 15px; text-align: center; background-color: #fff">-->
    <!--      <el-col>-->
    <!--        <span style="font-size: 20px; font-family: PingFang SC">{{teamName}} / {{appName}}</span>-->
    <!--      </el-col>-->
    <!--    </el-row>-->

    <el-row style="padding-top: 25px;padding-left: 15px;background-color: #fff">
      <el-col>
        <!--        <el-autocomplete size="small" v-model="state4" :fetch-suggestions="autoCompleteData"-->
        <!--                         popper-class="my-autoComplete" spellcheck="false"-->
        <!--                         placeholder="请输入配置项查询" @select="queryByKey" clearable>-->
        <!--          <template slot-scope="{item}">-->
        <!--            <div class="value">{{ item.value }}</div>-->
        <!--            <span class="keyDes">{{ item.keyDes }}</span>-->
        <!--          </template>-->
        <!--        </el-autocomplete>-->
        <el-input v-model="state4" spellcheck="false" @select="queryByKey" placeholder="按配置项名称模糊搜索"
                  style="width:200px;" size="small" clearable="true"/>
        <el-button size="small" type="primary" icon="el-icon-plus" style="margin-left: 10px" @click="configKeyAdd">新增
        </el-button>
        <el-button size="small" type="primary" icon="el-icon-refresh" @click="configKeyHandleSearch">刷新列表
        </el-button>
        <el-button size="small" type="primary" icon="el-icon-download" @click="exportFile">导出配置项文件
        </el-button>
        <el-button size="small" type="primary" icon="el-icon-upload" @click="btnClick">上传配置项文件</el-button>
        <el-button size="small" type="primary" icon="el-icon-bell" @click="pushAllConfigKey" :disabled="pushEnable">
          一键下发
        </el-button>
        <el-button size="small" type="danger" icon="el-icon-warning" @click="batchDelConfigKey" :disabled="pushEnable">
          批量删除配置项
        </el-button>
        <el-button size="small" type="warning" icon="el-icon-s-promotion" @click="syncConfigKey">
          批量同步配置项至其他环境
        </el-button>
      </el-col>
    </el-row>


    <!--    <el-row style="padding: 25px 15px 15px 15px;background-color: #fff">-->
    <!--      <Alert show-icon style="font-size: 13px">-->
    <!--        「服务订阅者IP」表示当前订阅该配置的实例IP；当配置项过长时会自动省略，鼠标悬浮在配置项上即可查看全部。「服务订阅者IP」表示-->
    <!--        订阅了这些配置项的实例，当实例停止时，这些IP就会消失。-->
    <!--      </Alert>-->
    <!--    </el-row>-->

    <el-row style="padding: 28px 16px 16px 16px;background-color: #fff">
      <div style="font-size: 13px" class="my-el-alert my-el-alert--info">
        <el-col>
          <div style="color: #303133;font-weight: bold">提示</div>
          <div style="padding-top: 5px;color: #777777;font-size: 12px">
            配置项过长在页面显示时会自动省略后半部分，鼠标悬停在配置项上即可查看全部以及更多信息。
            <span style="font-weight: bold">对于Spring或springboot的原生注解或占位符修饰的配置项只提供托管，不支持动态变更与灰度下发。</span>
            <br>
            <span style="font-weight: bold">对于使用配置中心的提供的注解和占位符修饰的配置项提供动态变更与灰度下发。</span>
            「服务订阅者IP」表示实时订阅了这些配置项的实例IP，当实例停止时，这些IP也随之消失。
          </div>
        </el-col>
      </div>
    </el-row>

    <el-row style="background-color: #ffffff;">

      <el-col span="18" class="el-table-my">
        <el-col>
          <el-table :data="configDataList" stripe fit v-loading="configLoading" style="width: 100%;"
                    size="medium" @selection-change="handleSelectionChange">
            <!--            <el-table-column type="index" label="#" min-width="50px"/>-->
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="configKey" label="配置项" width="250px" class-name="position-relative">
              <template slot-scope="scope">
                <el-tooltip v-if="scope.row.configKey" :content="scope.row.configKey" placement="right" effect="dark">
                  <span slot="content">
                    配置项:
                    {{scope.row.configKey}}
                     <br/> <br/>
                    生效值:
                    {{scope.row.configValue}}
                     <br/> <br/>
                    配置项版本号:
                    {{scope.row.keyVersion}}
                    <br/> <br/>
                    配置项描述:
                    {{scope.row.keyDes}}
                  </span>
                  <div v-if="scope.row.machineNodeDataList.length === 0"
                       style="border:rgb(217,216,221) 5px; background-color: rgb(241,241,243);border-radius: 4px;">
                    <div style="padding: 5px 5px 5px 8px;font-weight: bold">
                      <span v-if="scope.row.configKey.length <= 25" style="padding: 5px;">
                        {{scope.row.configKey}}</span>
                      <span v-if="scope.row.configKey.length > 25">
                        {{scope.row.configKey && scope.row.configKey.substr(0,25)}}
                        <span style="cursor: pointer;">...</span>
                      </span>
                    </div>
                  </div>
                  <div v-if="scope.row.machineNodeDataList.length > 0"
                       style="border:rgba(25,190,107,0.71) 5px; background-image: linear-gradient(to right, rgba(25,190,107,0.93) , #67abff);border-radius: 4px;">
                    <div style="padding: 5px 5px 5px 8px;font-weight: bold;color: #FFFFFF;overflow: hidden">
                      <span v-if="scope.row.configKey.length <= 25" style="padding: 5px;">
                        {{scope.row.configKey}}</span>
                      <span v-if="scope.row.configKey.length > 25">
                        {{scope.row.configKey && scope.row.configKey.substr(0,25)}}
                        <span style="cursor: pointer;">...</span>
                      </span>
                    </div>
                    <div class="container">
                      <div class="wave"></div>
                    </div>
                  </div>

                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="生效值 / 预备值" min-width="205px">
              <template slot-scope="scope">
                <el-row span="2">
                  <span v-if="!scope.row.configValue" style="padding: 5px;color:#c5c8ce;">无生效值</span>
                  <span v-if="scope.row.configValue && scope.row.configValue.length <= 45"
                        style="padding: 5px;font-weight: bold">{{scope.row.configValue}}</span>
                  <el-tooltip v-if="scope.row.configValue && scope.row.configValue.length > 45"
                              :content="scope.row.configValue" placement="top" effect="dark">
                    <div style="padding: 5px;font-weight: bold">
                      <span v-if="scope.row.configValue.length > 45">
                        {{scope.row.configValue && scope.row.configValue.substr(0,45)}}
                       <span style="cursor: pointer;">...</span>
                      </span>
                    </div>
                  </el-tooltip>
                </el-row>

                <Divider dashed style="margin: 12px"/>

                <el-row span="2">
                  <span v-if="!scope.row.edit && !scope.row.preConfigValue"
                        style="padding: 5px;color:#c5c8ce;font-weight: bold">
                    无预备值
                  </span>
                  <span v-if="!scope.row.edit && scope.row.preConfigValue && scope.row.preConfigValue.length <= 45"
                        style="color:#c5c8ce;padding: 5px;font-weight: bold">
                    {{scope.row.preConfigValue}}
                  </span>
                  <el-tooltip v-if="!scope.row.edit && scope.row.preConfigValue && scope.row.preConfigValue.length > 45"
                              :content="scope.row.preConfigValue" placement="top" effect="dark">
                    <div style="padding-left: 5px;color:#c5c8ce;font-weight: bold;display: inline-block">
                      <span v-if="scope.row.preConfigValue.length > 45">
                        {{scope.row.preConfigValue && scope.row.preConfigValue.substr(0,45)}}
                       <span style="cursor: pointer;">...</span>
                      </span>
                    </div>
                  </el-tooltip>
                </el-row>

              </template>

              <!--              <el-tooltip v-if="scope.row.keyDes" :content="scope.row.keyDes" placement="top" effect="dark">-->
              <!--                <div style="font-weight: bold">-->
              <!--                  <div style="padding: 5px 5px 5px 8px;">-->
              <!--                      <span v-if="scope.row.keyDes.length <= 10" style="padding: 5px;">-->
              <!--                        {{scope.row.keyDes}}</span>-->
              <!--                    <span v-if="scope.row.keyDes.length > 10">-->
              <!--                        {{scope.row.keyDes && scope.row.keyDes.substr(0,10)}}-->
              <!--                        <span style="cursor: pointer;">...</span>-->
              <!--                      </span>-->
              <!--                  </div>-->
              <!--                </div>-->
              <!--              </el-tooltip>-->

            </el-table-column>
            <!--            <el-table-column prop="preConfigValue" label="预备值" min-width="120px">-->
            <!--              <template slot-scope="scope">-->
            <!--                <span>{{scope.row.preConfigValue && scope.row.preConfigValue.substr(0,50)}}</span>-->
            <!--                <el-tooltip v-if="scope.row.preConfigValue &&　scope.row.preConfigValue.length > 50"-->
            <!--                            :content="scope.row.preConfigValue" placement="top" effect="dark">-->
            <!--                  <span style="cursor: pointer;color: #4a90e2;">...</span>-->
            <!--                </el-tooltip>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
            <!--            <el-table-column prop="keyVersion" label="版本号" width="150px">-->
            <!--              <template slot-scope="scope">-->
            <!--                <el-tag size="mini">{{scope.row.keyVersion}}</el-tag>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
            <!--            <el-table-column prop="tag" label="订阅状态" width="220">-->
            <!--              <template slot-scope="scope">-->
            <!--                <div v-for="item in scope.row.machineNodeDataList" style="padding: 2px">-->
            <!--                  <el-row>-->
            <!--                    <el-col :span="3">-->
            <!--                      &lt;!&ndash;徽标&ndash;&gt;-->
            <!--                      <Badge v-if="item.needUpdate === filterData[0].value" status="processing"/>-->
            <!--                      <Badge v-if="item.needUpdate === filterData[1].value" status="warning"/>-->
            <!--                      <Badge v-if="item.needUpdate === filterData[2].value" status="error"/>-->
            <!--                    </el-col>-->
            <!--                    <el-col :span="10">-->
            <!--                      &lt;!&ndash;host&ndash;&gt;-->
            <!--                      <el-tag size="mini" v-if="item.needUpdate === filterData[0].value" type="success">-->
            <!--                        {{item.machineHost}}-->
            <!--                      </el-tag>-->
            <!--                      <el-tag size="mini" v-if="item.needUpdate === filterData[1].value" type="warning">-->
            <!--                        {{item.machineHost}}-->
            <!--                      </el-tag>-->
            <!--                      <el-tag size="mini" v-if="item.needUpdate === filterData[2].value" type="danger">-->
            <!--                        {{item.machineHost}}-->
            <!--                      </el-tag>-->
            <!--                    </el-col>-->
            <!--                    <el-col :span="8" style="padding-left: 30px">-->
            <!--                      &lt;!&ndash;生效状态&ndash;&gt;-->
            <!--                      <el-tag size="mini" v-if="item.needUpdate === filterData[0].value" type="success">-->
            <!--                        {{filterData[0].text}}-->
            <!--                      </el-tag>-->
            <!--                      <el-tag size="mini" v-if="item.needUpdate === filterData[1].value" type="warning">-->
            <!--                        {{filterData[1].text}}-->
            <!--                      </el-tag>-->
            <!--                      <el-tag size="mini" v-if="item.needUpdate === filterData[2].value" type="danger">-->
            <!--                        {{filterData[2].text}}-->
            <!--                      </el-tag>-->
            <!--                    </el-col>-->
            <!--                  </el-row>-->
            <!--                </div>-->
            <!--                <div v-if="scope.row.machineNodeDataList.length === 0" style="padding-top: 5px">-->
            <!--                  <el-row>-->
            <!--                    <el-col span="3">-->
            <!--                      <Badge status="default"/>-->
            <!--                    </el-col>-->
            <!--                    <el-col span="13">-->
            <!--                      <el-tag size="mini" type="info">无订阅数据</el-tag>-->
            <!--                    </el-col>-->
            <!--                  </el-row>-->
            <!--                </div>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
            <!--            <el-table-column prop="keyDes" label="配置项描述" width="220px">-->
            <!--              <template slot-scope="scope">-->
            <!--                <el-tooltip v-if="scope.row.keyDes" :content="scope.row.keyDes" placement="top" effect="dark">-->
            <!--                  <div style="font-weight: bold">-->
            <!--                    <div style="padding: 5px 5px 5px 8px;">-->
            <!--                      <span v-if="scope.row.keyDes.length <= 10" style="padding: 5px;">-->
            <!--                        {{scope.row.keyDes}}</span>-->
            <!--                      <span v-if="scope.row.keyDes.length > 10">-->
            <!--                        {{scope.row.keyDes && scope.row.keyDes.substr(0,10)}}-->
            <!--                        <span style="cursor: pointer;">...</span>-->
            <!--                      </span>-->
            <!--                    </div>-->
            <!--                  </div>-->
            <!--                </el-tooltip>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
            <el-table-column label="版本号" width="180px">
              <template slot-scope="scope" v-if="scope.row.keyVersion">
                <el-tag size="mini" type="info">{{scope.row.keyVersion}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220px" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="medium" @click="configKeyEdit(scope.row)">编辑</el-button>
                <Divider type="vertical"/>
                <el-button type="text" size="medium" @click="deleteConfigKey(scope.row)">删除</el-button>
                <Divider type="vertical" v-if="scope.row.preConfigValue || scope.row.pushStatus === 0"/>
                <el-button type="text" size="medium" v-if="scope.row.preConfigValue" @click="pushConfigKey(scope.row)">
                  下发
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>

        <!--分页工具条-->
        <el-col :span="24" class="toolbar" style="margin-top: 10px">
          <el-pagination layout="total, sizes, prev, pager, next, jumper" style="float:right;"
                         @size-change="configKeyOnSizeChange"
                         @current-change="configKeyOnPageChange"
                         :page-sizes="[5,10,20,50,100,150,200]"
                         :current-page.sync="page"
                         :page-count="totalPage"
                         :page-size="size"
                         :total="total">
          </el-pagination>
        </el-col>
      </el-col>

      <el-col span="1">
        <Divider type="vertical" style="height: 350px"/>
      </el-col>

      <el-col span="5" class="el-table-my-1">
        <el-table class="my-ip-table" :data="consumerIpList" fit height="330" v-loading="consumerTableLoading"
                  style="width: 100%;" size="medium">
          <el-table-column prop="tag" label="服务订阅者IP" min-width="220px">
            <template slot-scope="scope">
              <el-row>
                <el-col :span="2">
                  <!--徽标-->
                  <Badge status="processing" color="purple"/>
                </el-col>
                <el-col :span="8">
                  <!--host-->
                  <el-tag size="mini" type="success">{{scope.row}}</el-tag>
                </el-col>
                <el-col :span="6" style="padding-left: 50px">
                  <!--生效状态-->
                  <el-tag size="mini" type="success">已订阅</el-tag>
                </el-col>
              </el-row>
            </template>
          </el-table-column>
        </el-table>
      </el-col>

    </el-row>

    <!--dialog-->
    <el-dialog :title="configAddOrEditStatus.title" :close-on-click-modal="false"
               width="32%" size="tiny" :visible.sync="configAddOrEditFormVisible">
      <el-form :model="configAddOrEditForm" ref="configAddOrEditForm" label-width="100px">
        <el-form-item label="配置项" prop="configKey">
          <el-input size="small" v-model="configAddOrEditForm.configKey" auto-complete="off" :disabled="configKeyEnable"
                    placeholder="请填写配置项" spellcheck="false"/>
        </el-form-item>

        <!--        <el-form-item label="类型" prop="keyType">-->
        <!--          <el-select size="small" style="width: 250px" v-model="configAddOrEditForm.keyType"-->
        <!--                     placeholder="请选择配置项类型" clearable>-->
        <!--            <el-option-->
        <!--              v-for="(item,index) in keyTypeList"-->
        <!--              :label="item.value"-->
        <!--              :value="item.value"-->
        <!--              :key="value">-->
        <!--            </el-option>-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->

        <el-form-item label="生效值" prop="configValue">
          <el-input type="textarea" size="small" v-model="configAddOrEditForm.configValue" rows="5" disabled="true"
                    auto-complete="off"
                    placeholder="请填写预备值" spellcheck="false"/>
        </el-form-item>

        <el-form-item label="预备值" prop="preConfigValue">
          <el-input type="textarea" size="small" v-model="configAddOrEditForm.preConfigValue" rows="5"
                    auto-complete="off"
                    placeholder="请填写预备值" spellcheck="false"/>
        </el-form-item>

        <el-form-item label="版本号" prop="keyVersion">
          <el-input size="small" v-model="configAddOrEditForm.keyVersion" auto-complete="off" :disabled="true"
                    placeholder="由系统自动生成"/>
        </el-form-item>

        <el-form-item label="键描述" prop="keyDes">
          <el-input type="textarea" size="small" v-model="configAddOrEditForm.keyDes" auto-complete="off" rows="3"
                    placeholder="请填写键描述" spellcheck="false"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="configCancel">取 消</el-button>
        <el-button size="mini" type="primary" @click="configAddOrEdit">确 定</el-button>
      </div>
    </el-dialog>


    <!--下发弹窗-->
    <el-dialog title="下发" :close-on-click-modal="false"
               width="32%" size="tiny" :visible.sync="pushFormVisible">

      <el-form :model="pushKeyForm" ref="pushKeyForm" label-width="100px">
        <el-form-item label="key" prop="configKey">
          <el-input size="small" v-model="pushKeyForm.configKey" auto-complete="off" :disabled="true"
                    placeholder="请填写key" spellcheck="false"/>
        </el-form-item>

        <el-form-item label="预备值" prop="preConfigValue">
          <el-input type="textarea" size="small" v-model="pushKeyForm.preConfigValue" rows="5"
                    auto-complete="off" spellcheck="false" :disabled="true"/>
        </el-form-item>
        <div style="color: #FF4949;font-size: 12px;margin-left: 100px;margin-top: -10px;margin-bottom: 20px">
          <p>* 下发时生效值将会被非空的预备值替换, 同时下发到客户端, 请谨慎操作.</p>
          <p>* 在第一次下发的时候, 默认为下发到所有实例, 需要等待实例做出响应.</p>
        </div>
        <el-form-item v-show="showRadio" label="下发策略">
          <RadioGroup v-model="pushType" @on-change="onRadioChanged">
            <Radio label="1">
              <span style="font-size: 13px">全量下发</span>
            </Radio>
            <Radio label="0">
              <span style="font-size: 13px">灰度下发</span>
            </Radio>
          </RadioGroup>
        </el-form-item>

        <el-form-item v-show="showRadio & showSelect" label="选择实例">
          <el-select v-model="selectData" multiple placeholder="请选择实例" size="small">
            <el-option
              v-for="item in machineHostData"
              :key="item.machineHost"
              :label="item.machineHost"
              :value="item.machineHost">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="cancelPushConfigKey">取 消</el-button>
        <el-button size="mini" type="primary" @click="confirmPushConfigKey">确 定</el-button>
      </div>
    </el-dialog>


    <el-dialog title="同步配置项到其他环境" :close-on-click-modal="false"
               width="40%" size="tiny" :visible.sync="syncFormVisible">

      <el-form :model="syncKeyForm" ref="syncKeyForm" label-width="150px">
        <el-form-item label="同步的环境" style="font-weight: bold">
          <RadioGroup v-model="envIdForSync">
            <Radio label="0" v-if="envId !== '0'">
              <span style="font-size: 13px">开发环境</span>
            </Radio>
            <Radio label="1" v-if="envId !== '1'">
              <span style="font-size: 13px">测试环境</span>
            </Radio>
            <Radio label="2" v-if="envId !== '2'">
              <span style="font-size: 13px" class="warn">预发环境</span>
            </Radio>
            <Radio label="3" v-if="envId !== '3'">
              <span style="font-size: 13px" class="danger">生产环境</span>
            </Radio>
          </RadioGroup>
        </el-form-item>

        <Divider dashed style="margin-top: -20px"/>

        <div v-for="item in multipleSelectionData" style="padding-top: 20px">
          <el-form-item label="配置项/预备值" prop="configKey" style="font-weight: bold">
            <el-input size="small" v-model="item.key" auto-complete="off" :disabled="true"
                      spellcheck="false"/>
          </el-form-item>

          <el-form-item label="" prop="configValue" style="margin-top: -20px">
            <el-input type="textarea" size="small" v-model="item.value" rows="1"
                      auto-complete="off" spellcheck="false"/>
          </el-form-item>
        </div>
        <Divider dashed style="padding-left: 50px"/>
        <div style="color: #20a0ff;font-size: 12px;margin-left: 50px;margin-top: 10px;margin-bottom: 20px">
          <p>* 同步时「配置项」与「预备值」将会发送到选中的环境中去，</p>
          <p>* <span style="font-weight: bold">不会下发到客户端</span>，请在同步后前往对应环境手动下发.</p>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="cancelSyncConfigKey">取 消</el-button>
        <el-button size="mini" type="primary" @click="confirmSyncConfigKey">确 定</el-button>
      </div>
    </el-dialog>

    <div>
      <input ref="file" type="file" @change="uploadFile" v-show="false">
    </div>


  </section>
</template>

<script>
  import eventBus from '../../common/js/eventBus.js'

  let baseUrL = '/bridge';

  export default {
    data() {
      return {
        appList: {},
        total: 0,
        totalPage: 0,
        size: 50,
        page: 1,
        activeIndex: '0',
        teamName: '',
        appName: '',
        appId: '',
        env: '',
        envId: '',
        envIdForSync: '',
        state4: '',
        pushType: '1',
        clearEnable: true,
        showSelect: false,
        showRadio: false,
        selectData: [],
        configKeyList: [],
        configKey: '',
        menuName: '点击选择系统',
        selectorList: {},
        keyTypeList: [],
        configAddOrEditForm: {},
        pushKeyForm: {},
        configKeyEnable: false,
        configAddOrEditFormVisible: false,
        pushFormVisible: false,
        syncFormVisible: false,
        syncKeyForm: {},
        multipleSelectionData: [],
        configAddOrEditStatus: {
          title: '新增',
          value: 0,
          rules: '',
        },
        filterData: [{text: '正常', value: 0}, {text: '待下发', value: 1}, {text: '未使用', value: 2}],
        configLoading: true,
        consumerTableLoading: true,
        configDataList: [],
        consumerIpList: [],
        machineHostData: [],
        multipleSelection: [],
        isPageOpen: false,
        headers: {
          authorization: 'authorization-text',
        },
        uploadUrl: '',
        pushEnable: false,
      };
    },
    methods: {

      // 批量删除
      batchDelConfigKey() {
        if (this.multipleSelection.length === 0) {
          this.$message({showClose: true, message: "请选择要删除的配置项", type: 'info'});
          return;
        }
        let idList = [];
        for (let i = 0; i < this.multipleSelection.length; i++) {
          idList.push(this.multipleSelection[i].id);
        }
        let param = {
          idList: idList,
        };
        this.$confirm('该操作会删除您已选中的配置项，确认删除么?', '提示', {type: 'warning'}).then(() => {
          let url = baseUrL + '/batchDeleteConfigKey';
          this.$http.post(url, JSON.stringify(param), {emulateJSON: true}).then(function (res) {
            if (res.body.success) {
              this.$message({showClose: true, message: "操作成功", type: 'success'});
              this.searchTab(this.configKey);
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
            }
          }).catch((err) => {
            console.log('error', err);
            this.$message({showClose: true, message: err, type: 'error'});
          })
        })

      },
      // 多选框
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },

      // table数据
      doWhenSelectChanged(tab, event) {
        if (event.length === 3) {
          this.teamId = event[2].split('-')[0];
          this.appId = event[2].split('-')[1];
          localStorage.setItem('teamId', this.teamId);
          localStorage.setItem('appId', this.appId);
          // 查询标签
          this.searchTag();
          // 查询列表数据
          this.searchTab(this.configKey);
        }
      },
      // 根据key查询
      queryByKey(item) {
        this.configKey = item.value;
        // 查询列表数据
        this.searchTab(this.configKey);
      },
      // RadioButton的change事件
      onRadioChanged(val) {
        if (val === '0') {
          this.showSelect = true
        }
        if (val === '1') {
          this.showSelect = false
        }
        this.pushType = val;
        console.log('--------->', val);
      },
      // 在输入框输入数据的时候实时搜索
      autoCompleteData(queryString, cb) {
        if (this.appId) {
          let params = {};
          params.appId = this.appId;
          params.envId = localStorage.getItem('envId');
          let url = baseUrL + '/queryConfigKeyList';
          this.$http.get(url, {params: params}).then(function (res) {
            this.configKeyList = res.body.result;
            let results = queryString ? this.configKeyList.filter(this.createStateFilter(queryString)) : this.configKeyList;
            cb(results);
          }).catch(err => {
            console.log('error', err);
          });
        }
      },

      createStateFilter(queryString) {
        return (state) => {
          return (state.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
        };
      },

      // 删除
      deleteConfigKey(row) {
        this.$confirm('确认删除该记录吗?', '提示', {type: 'warning'}).then(() => {
          let url = baseUrL + '/deleteConfigKey';
          this.$http.post(url, {id: row.id}, {emulateJSON: true}).then(function (res) {
            if (res.body.success) {
              this.teamAddOrEditFormVisible = false;
              this.$message({showClose: true, message: "操作成功", type: 'success'});
              this.searchTab(this.configKey);
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
            }
          }).catch((err) => {
            console.log('error', err);
            this.$message({showClose: true, message: err, type: 'error'});
          })
        })
      },

      // 同步配置项到其他环境
      syncConfigKey() {
        if (this.multipleSelection.length === 0) {
          this.$message({showClose: true, message: "请选择要同步的配置项", type: 'info'});
          return;
        }
        this.multipleSelectionData = [];
        for (let i = 0; i < this.multipleSelection.length; i++) {
          const data = {};
          data.key = this.multipleSelection[i].configKey;
          data.value = this.multipleSelection[i].configValue;
          this.multipleSelectionData.push(data);
        }
        this.syncFormVisible = true;
      },

      // 取消同步至其他环境
      cancelSyncConfigKey() {
        this.syncFormVisible = false;
        this.$refs['syncKeyForm'].clearValidate();
        this.envIdForSync = '';
      },

      // 确认同步至其他环境
      confirmSyncConfigKey() {
        if (this.multipleSelectionData.length === 0) {
          this.$message({showClose: true, message: "请选择要同步的配置项", type: 'info'});
          return;
        }
        if (!this.envIdForSync) {
          this.$message({showClose: true, message: "请选择需要同步的环境", type: 'error'});
          return;
        }
        const param = JSON.stringify({
          envId: this.envIdForSync,
          appId: this.appId,
          configKvVOList: this.multipleSelectionData,
        });
        let url = baseUrL + '/syncConfigKey';
        this.$http.post(url, param, {emulateJSON: true}).then(function (res) {
          if (res.body.success) {
            this.syncFormVisible = false;
            this.$message({showClose: true, message: "操作成功", type: 'success'});
            this.searchTab(this.configKey);
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
          }
        }).catch((err) => {
          console.log('error', err);
          this.$message({showClose: true, message: err, type: 'error'});
        })
      },

      // 打开下发弹窗
      pushConfigKey(row) {
        this.pushFormVisible = true;
        // 显示编辑页面的默认值
        this.pushKeyForm = {
          configKey: row.configKey,
          preConfigValue: row.preConfigValue,
          keyVersion: row.keyVersion,
          keyDes: row.keyDes,
          id: row.id,
        };
        this.selectData = [];
        this.machineHostData = row.machineNodeDataList;
        if (this.machineHostData.length === 0) {
          this.showRadio = false;
        } else {
          this.showRadio = true;
        }
        this.pushType = '1';
        this.showSelect = false;
      },

      // 取消下发
      cancelPushConfigKey() {
        this.pushFormVisible = false;
        this.machineHostData = [];
        this.selectData = [];
        this.pushType = '1';
        this.showSelect = false;
        this.$refs['pushKeyForm'].clearValidate();
      },

      // 确认下发
      confirmPushConfigKey() {
        this.pushFormVisible = false;
        NProgress.start();
        let params = JSON.stringify({
          appId: this.appId,
          id: this.pushKeyForm.id,
          pushType: this.pushType,
          envId: localStorage.getItem('envId'),
          machineList: this.selectData,
        });
        let url = baseUrL + '/pushConfigKv';
        this.$http.post(url, params, {emulateJSON: true}).then(function (res) {
          if (res.body.success) {
            this.$message({showClose: true, message: "操作成功", type: 'success'});
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
          }
          // 查询列表数据
          this.searchTab(this.configKey);
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },


      // 一键下发
      pushAllConfigKey() {
        let env = localStorage.getItem('env');
        let teamName = this.teamName;
        let appName = this.appName;
        if (!teamName || !appName) {
          this.$message({showClose: true, message: "请先选择需要一键下发的系统", type: 'warning'});
          return;
        }
        let des = '【' + teamName + ' - ' + appName + ' - ' + env + '】';
        this.$confirm('该操作会将' + des + '的所有配置项全量下发到所有实例，您确认下发吗?', '提示', {type: 'warning'}).then(() => {
          NProgress.start();
          this.configLoading = true;
          this.pushEnable = true;
          let params = {};
          params.envId = localStorage.getItem('envId');
          params.appId = this.appId;
          let url = baseUrL + '/pushAllConfigKv';
          this.$http.get(url, {params: params}).then(function (res) {
            if (res.body.success) {
              this.$message({showClose: true, message: "操作成功", type: 'success'});
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
            }
            this.configLoading = false;
            this.pushEnable = false;
            // 查询列表数据
            this.searchTab(this.configKey);
          }).catch(err => {
            console.log('error', err);
          });
          NProgress.done();
        })
      },

      // 新增或编辑
      configAddOrEdit() {
        if (this.appId.length === 0) {
          this.$message({showClose: true, message: '请选择系统后操作', type: 'error'});
          return false;
        }
        if (!this.configAddOrEditForm.configKey) {
          this.$message({showClose: true, message: "请填写配置项", type: 'error'});
          return false;
        }
        if (this.configAddOrEditForm.configKey.length > 1024) {
          this.$message({showClose: true, message: "配置项过长，不能超过1024个字符", type: 'error'});
          return false;
        }
        if (!this.configAddOrEditForm.keyDes) {
          this.$message({showClose: true, message: "请输入配置项的描述", type: 'error'});
          return false;
        }
        if (this.configAddOrEditForm.keyDes.length > 1024) {
          this.$message({showClose: true, message: "描述过长，不能超过1024个字符", type: 'error'});
          return false;
        }
        let url;
        if (this.configAddOrEditStatus.value === 0) {
          if (!this.configAddOrEditForm.preConfigValue) {
            this.$message({showClose: true, message: "请输入预备值", type: 'error'});
            return false;
          }
          url = baseUrL + '/addConfigKv';
          this.configAddOrEditForm.envId = localStorage.getItem('envId');
        } else if (this.configAddOrEditStatus.value === 1) {
          url = baseUrL + '/editConfigKv';
        }
        this.configAddOrEditForm.appId = this.appId;
        this.$http.post(url, this.configAddOrEditForm).then(function (res) {
          if (res.body.success) {
            this.configAddOrEditFormVisible = false;
            this.$message({showClose: true, message: "操作成功", type: 'success'});
            this.searchTab(this.configKey);
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
            this.configAddOrEditFormVisible = true;
            this.searchTab(this.configKey);
          }
        }).catch(err => {
          this.$message({showClose: true, message: '表单校验出错', type: 'warning'});
          return false;
        });
      },

      // 取消
      configCancel() {
        this.configAddOrEditFormVisible = false;
        this.$refs['configAddOrEditForm'].clearValidate();
      },

      // 新增
      configKeyAdd() {
        this.configAddOrEditStatus = {
          title: '新增',
          value: 0
        };
        this.configKeyEnable = false;
        this.configAddOrEditFormVisible = true;
        // 显示新增页面的默认值
        this.configAddOrEditForm = {
          configKey: '',
          preConfigValue: '',
          configValue: '',
          keyVersion: '',
          keyDes: '',
          id: '',
          envId: '',
          // keyType: '',
        };
      },

      // 编辑
      configKeyEdit(row) {
        this.configAddOrEditStatus = {
          title: '编辑',
          value: 1
        };
        this.configKeyEnable = true;
        this.configAddOrEditFormVisible = true;
        // 显示编辑页面的默认值
        this.configAddOrEditForm = {
          configKey: row.configKey,
          preConfigValue: row.preConfigValue,
          configValue: row.configValue,
          keyVersion: row.keyVersion,
          keyDes: row.keyDes,
          id: row.id,
          envId: row.envId,
          // keyType: row.keyType,
        };
      },

      // 分页
      configKeyOnPageChange(val) {
        this.page = val;
        this.searchTab(this.configKey);
      },
      // 分页
      configKeyOnSizeChange(val) {
        this.size = val;
        this.searchTab(this.configKey);
      },

      // 查询
      configKeyHandleSearch() {
        if (this.appId) {
          this.searchTag();
          this.searchTab(this.configKey);
        }
      },

      // 导出文件
      exportFile() {
        let teamName = this.teamName;
        let appName = this.appName;
        if (!teamName || !appName) {
          this.$message({showClose: true, message: "请先选择需要导出配置项文件的系统", type: 'warning'});
          return;
        }
        NProgress.start();
        let judgeUrl = baseUrL + '/judgeExportFile';
        let exportUrl = baseUrL + '/exportFile';
        let params = {};
        params.appId = this.appId;
        params.envId = localStorage.getItem('envId');
        this.$http.get(judgeUrl, {params: params}).then(function (res) {
          if (!res.body.success) {
            this.$message({showClose: true, message: res.body.message, type: 'warning'});
          } else {
            window.location.href = exportUrl + '?appId=' + this.appId + '&envId=' + localStorage.getItem('envId');
          }
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 点击按钮出发input框的上传事件
      btnClick() {
        let teamName = this.teamName;
        let appName = this.appName;
        if (!teamName || !appName) {
          this.$message({showClose: true, message: "请先选择需要上传配置项文件的系统", type: 'warning'});
          return;
        }
        this.$refs.file.click();
      },

      // 上传文件
      uploadFile(event) {
        console.log(event);
        let url = baseUrL + "/uploadProperties";
        const fileObj = event.target.files[0];
        if (!fileObj) {
          return;
        }
        const size = fileObj.size / (1024 * 1024);
        if (size >= 10) {
          this.$message({showClose: true, message: "上传文件不能大于10MB", type: 'error'});
          return;
        }
        let formData = new FormData();
        formData.append("envId", localStorage.getItem('envId'));
        formData.append("appId", localStorage.getItem('appId'));
        formData.append("multipartFile", fileObj);
        this.$http.post(url, formData).then(res => {
          if (res.body.success) {
            this.$message({showClose: true, message: res.body.result, type: 'success'});
            this.searchTab(this.configKey);
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
          }
          this.$refs.file.value = '';
        }).catch(err => {
          console.log('error', err);
        });
      },

      // 查询标签数据
      searchTag() {
        NProgress.start();
        let url = baseUrL + '/queryTeamNameAndAppNameByAppId';
        let params = {};
        params.appId = this.appId;
        this.$http.get(url, {params: params}).then(function (res) {
          this.teamName = res.body.result.teamName;
          this.appName = res.body.result.appName;
          this.menuName = this.teamName + ' / ' + this.appName;
          localStorage.setItem('teamName', this.teamName);
          localStorage.setItem('appName', this.appName);
          // localStorage.setItem('menuName', this.menuName);
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 查询列表数据
      searchTab(configKey) {
        this.configLoading = true;
        this.consumerTableLoading = true;
        NProgress.start();
        let params = {};
        params.envId = localStorage.getItem('envId');
        params.appId = this.appId;
        params.page = this.page;
        params.size = this.size;
        if (configKey) {
          params.configKey = configKey;
        }
        let url = baseUrL + '/queryConfigDataByAppId';
        this.$http.get(url, {params: params}).then(function (res) {
          this.configDataList = res.body.result;
          this.total = res.body.total;
          this.totalPage = res.body.totalPage;
          this.configLoading = false;
        }).catch(err => {
          console.log('error', err);
        });

        // 查询订阅情况
        let consumerUrl = baseUrL + '/queryConsumerHost';
        this.$http.get(consumerUrl, {params: params}).then(function (res) {
          this.consumerIpList = res.body.result;
          this.consumerTableLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 查询下拉框筛选列表
      getSelectorData() {
        let params = {};
        let url = baseUrL + '/getSelectorData';
        this.$http.get(url, {params: params}).then(function (res) {
          this.selectorList = res.body.result;
        }).catch(err => {
          console.log('error', err);
        });
      },

      getKeyTypeList() {
        let params = {};
        let url = baseUrL + '/queryKeyTypeList';
        this.$http.get(url, {params: params}).then(function (res) {
          this.keyTypeList = res.body.result;
        }).catch(err => {
          console.log('error', err);
        });
      }
    },
    mounted() {
      this.isPageOpen = true;
      eventBus.$emit('firstColumn', '配置项');
      eventBus.$emit('secondColumn', '配置管理');
      eventBus.$emit('toPage', '/config_manager');
      this.getSelectorData();
      this.getKeyTypeList();
      this.teamId = localStorage.getItem('teamId');
      this.appId = localStorage.getItem('appId');
      this.teamName = localStorage.getItem('teamName') ? localStorage.getItem('teamName') : '';
      this.appName = localStorage.getItem('appName') ? localStorage.getItem('appName') : '';
      this.env = localStorage.getItem('env');
      this.envId = localStorage.getItem('envId');
      this.activeIndex = this.teamId + '-' + this.appId;
      if (this.appName && this.teamName) {
        this.menuName = this.teamName + ' / ' + this.appName;
      } else {
        this.menuName = '点击选择系统';
      }
      if (this.appId) {
        this.searchTab(this.configKey);
      } else {
        this.configLoading = false;
        this.consumerTableLoading = false;
      }
      eventBus.$on('change', () => {
        this.env = localStorage.getItem('env');
        this.envId = localStorage.getItem('envId');
        if (this.appId && this.isPageOpen) {
          this.searchTab(this.configKey);
        } else {
          this.configLoading = false;
          this.consumerTableLoading = false;
        }
      })
    },
    destroyed() {
      this.isPageOpen = false;
    },
    watch: {
      // 监听输入框，如果没有值并且appId存在则刷新table
      state4(val) {
        if (this.appId.length > 0) {
          this.configKey = val;
          // 查询列表数据
          this.searchTab(this.configKey);
        }
      }
    }
  }
</script>

<style scoped>

  .my-autoComplete li {
    line-height: normal;
    padding: 7px;
  }

  .my-autoComplete li .value {
    text-overflow: ellipsis;
    overflow: hidden;
  }

  .my-autoComplete li .keyDes {
    font-size: 12px;
    color: #b4b4b4;
  }

  .my-autoComplete li .highlighted .keyDes {
    color: #ddd;
  }

</style>

<style>
  .my-ip-table.el-table td {
    border: none;
  }

  .my-ip-table.el-table th.is-leaf {
    border: none;
  }

  .el-col.el-col-1 {
    width: 1%;
  }

  .my-el-alert.my-el-alert--info {
    background-color: #f4f4f5;
    color: #303133;
  }

  .my-el-alert {
    width: 100%;
    padding: 8px 16px;
    margin: 0;
    box-sizing: border-box;
    border-radius: 4px;
    position: relative;
    overflow: hidden;
    opacity: 1;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-transition: opacity .2s;
    transition: opacity .2s;
  }

  .container {
    position: absolute;
    width: 300px;
    height: 160px;
    padding: 5px;
    top: 35%;
    left: 100%;
    transform: translate(-50%, -50%);
    overflow: hidden;
  }
  .wave {
    position: relative;
    width: 200px;
    height: 200px;
    border-radius: 50%;
  }
  .wave::before,
  .wave::after{
    content: "";
    position: absolute;
    width: 400px;
    height: 400px;
    top: -20%;
    left: 50%;
    background-color: rgba(255, 255, 255, .3);
    border-radius: 45%;
    transform: translate(-50%, -70%) rotate(0);
    animation: rotate 6s linear infinite;
    z-index: 10;
  }
  .wave::after {
    border-radius: 47%;
    background-color: rgba(255, 255, 255, .5);
    transform: translate(-50%, -70%) rotate(0);
    animation: rotate 10s linear -5s infinite;
    z-index: 20;
  }

  @keyframes rotate {
    50% {
      transform: translate(-50%, -73%) rotate(180deg);
    }
    100% {
      transform: translate(-50%, -70%) rotate(360deg);
    }
  }


  .el-table .position-relative .cell {
    position: relative;
  }


</style>
