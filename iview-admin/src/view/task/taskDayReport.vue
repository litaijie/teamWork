<template>
  <div>
    <Row :gutter="20" style="margin-top: 2%;margin-bottom: 2%">
      <i-col :xs="12" :md="8" :lg="4" v-for="(infor, i) in this.data.taskCount" :key="`infor-${i}`" style="height: 80px;padding-bottom: 10px;">
        <infor-card shadow :color="infor.color" :icon="infor.icon" :icon-size="20">
          <count-to :end="infor.count" count-class="count-style"/>
<!--          <p>{{ infor.title }}</p>-->
        </infor-card>
      </i-col>

      <Col span="10" style="float: right">
        日期：
        <DatePicker :options="this.options3" @on-change="changeCreateTime" format="yyyy-MM-dd" type="date" placeholder="请选择创建时间"></DatePicker>
        <Button type="primary" icon="ios-search" style="margin-left: 2%" @click="listReport()">查询</Button>
        <Button type="success" icon="md-color-wand" style="margin-left: 2%" :disabled="this.data.dayReport" @click="saveReport()">生成报表</Button>
      </Col>
    </Row>

    <Row style="margin-top: 2%;margin-bottom: 2%">
      <Table size="small" :columns="columns1" :data="this.data.taskList" stripe></Table>
    </Row>
  </div>
</template>

<script>

import { saveTaskReport,listTaskDayReport } from '@/api/task.js'
import {formatDate} from '@/libs/tools.js'
import InforCard from '_c/info-card'
import Example from "../single-page/home/example";
import CountTo from '_c/count-to'


export default {
  name: "taskDayReport",
  components: {
    InforCard,
    CountTo
  },
  mounted() {
    this.listReport()
  },
  data () {
    return {
      param: {
        taskCreateTimeStart: null,
        taskLeader: null
      },
      taskCreateTime:[],
      data: [],
      options3: {
        disabledDate (date) {
          return date && date.valueOf() > Date.now();
        },
        shortcuts: [
          {
            text: '今天',
            value () {
              return new Date();
            }
          },
          {
            text: '昨天',
            value () {
              return formatDate((new Date()).valueOf()-86400000);
            }
          },
          {
            text: '前天',
            value () {
              return formatDate((new Date()).valueOf()-(86400000*2));
            }
          }
        ]
      },
      columns1: [
        {
          title: '任务名称',
          key: 'taskName'
        },
        {
          title: '负责人',
          key: 'taskLeader'
        },
        {
          title: '紧急程度',
          key: 'taskLevelDes'
        },
        {
          title: '延期（天）',
          key: 'taskLateDay',
          render: (h, params) => {
            const row = params.row;
            const color = row.taskLateDay === 0 ? 'success' : row.taskLateDay >0  ? 'error':'';
            const text = row.taskLateDay;

            return h('Tag', {
              props: {
                color: color
              }
            }, text);
          }
        },
        {
          title: '任务状态',
          key: 'taskStatusDes',
          render: (h, params) => {
            const row = params.row;
            const color = row.taskStatus === 0 ? 'error' : row.taskStatus === 1 ? 'warning' : row.taskStatus === 2 ?'success':'';
            const text = row.taskStatusDes;

            return h('Tag', {
              props: {
                // type: 'dot',
                color: color
              }
            }, text);
          }
        },
        {
          title: '备注',
          key: 'action',
          width: 300,
          align: 'center',
          render: (h, action) => {
            return h('div', [
              h('Input', {
                props: {
                  type: 'textarea',
                  autosize: {minRows: 2,maxRows: 3},
                  disabled: this.data.dayReport,
                  value: action.row.repComment
                },
                domProps:{
                  value: action.row.repComment
                },
                on: {
                  'on-change': (e) => {
                    this.data.taskList[action.index].repComment=e.target.value
                  }
                }
              }, '备注')
            ]);
          }
        }
      ]
    }
  },
  methods: {
    listReport(){
      listTaskDayReport(this.param).then(res=>{
        if(res.data.code===0){
          let list = res.data.data
          this.data=list
        }else {
          this.$Message.error('网络出错，请联系管理员！')
        }
      })
    },
    changeCreateTime(e){
      this.param.taskCreateTimeStart=e
    },
    saveReport(){
      this.$Modal.confirm({
        title: '确认操作',
        content: '是否确认生成日报',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          let datas = this.data.taskList
          let date = this.param.taskCreateTimeStart===null?formatDate(Date.now()):formatDate(this.param.taskCreateTimeStart)
          for(let t in datas){
            datas[t].repDate=date
          }

          if (datas){
            saveTaskReport(datas).then(e=>{
              if (e.data.code===0){
                this.$Message.success('生成报表成功')
                this.listReport()
              }
            })
          }else {
            this.$Message.warning('当前无任务提交')
          }
        }
      });

    }
  }
}
</script>

<style lang="less">
.count-style{
  font-size: 50px;
}
</style>
