<template>
  <div>
    <Row :gutter="15">
      <Col span="4">
        任务名称:
        <Input v-model="param.taskName" placeholder="请输入..." clearable/>
      </Col>
      <Col span="4">
        负责人：
        <Select v-model="param.taskLeader" placeholder="请输入..." clearable filterable>
          <Option v-for="item in this.userList" :value="item.username" :key="item.userId">{{ item.username }}</Option>
        </Select>
<!--        <Input v-model="param.taskLeader" placeholder="请输入..." clearable/>-->
      </Col>
      <Col span="4">
        所属类别：
        <Select v-model="param.taskCat1" placeholder="请选择所属类别" clearable>
          <Option value="软件">软件</Option>
          <Option value="硬件">硬件</Option>
        </Select>
      </Col>
      <Col span="4">
        所属分类：
        <Select v-model="param.taskCat2" placeholder="请选择所属分类" clearable filterable>
          <Option v-for="item in this.cat2List" :key="item.code" :value="item.value">{{item.value}}</Option>
        </Select>
      </Col>
      <Col span="4">
        紧急程度:
        <Select v-model="param.taskLevel" placeholder="请选择紧急程度" clearable>
          <Option value="一般">一般</Option>
          <Option value="急">急</Option>
          <Option value="加急">加急</Option>
        </Select>
      </Col>
      <Col span="4">
        任务状态:
        <Select v-model="param.taskStatus" placeholder="请选择紧急程度" clearable>
          <Option value="未完成">未完成</Option>
          <Option value="审核中">审核中</Option>
          <Option value="已完成">已完成</Option>
        </Select>
      </Col>
    </Row>
    <Row style="margin-top: 2%" :gutter="15">
      <Col span="4">
        问题原因:
        <Input v-model="param.taskReason" placeholder="请输入..." clearable/>
      </Col>
      <Col span="4">
        解决方案:
        <Input v-model="param.taskMethod" placeholder="请输入..." clearable/>
      </Col>
      <Col span="4">
        备注:
        <Input v-model="param.taskRemarks" placeholder="请输入..." clearable/>
      </Col>
      <Col span="5">
        创建时间：
        <DatePicker v-model="this.taskCreateTime" @on-change="changeCreateTime" format="yyyy-MM-dd HH:mm:ss" type="datetimerange" placeholder="请选择创建时间" style="width: 100%"></DatePicker>
      </Col>
      <Col span="5">
        完成时间：
        <DatePicker v-model="this.taskEndDate" @on-change="changeEndTime" format="yyyy-MM-dd HH:mm:ss" type="datetimerange" placeholder="请选择完成时间" style="width: 100%"></DatePicker>
      </Col>
    </Row>
    <Row style="margin-top: 2%;margin-bottom: 2%">
      <Row style="margin-bottom: 1%">
        <Button type="primary" icon="md-add-circle" style="margin-left: 1%" @click="add()">新增</Button>
        <Button type="primary" icon="md-add-circle" style="margin-left: 1%" @click="downloadTask()">下载</Button>
        <Button type="primary" icon="ios-search" style="float: right" @click="pageListTask()">查询</Button>
      </Row>
      <Table :loading="this.isLoading" size="small" :columns="columns1" :data="data" ref="selection" stripe @on-selection-change="selectTask" @on-row-dblclick="dblclickTable"></Table>
    </Row>
    <Row>
      <Page :total="this.total" :page-size="this.param.pageSize" :current="this.param.pageNum" size="small" @on-change="changePageNum" @on-page-size-change="changePageSize" show-total show-elevator show-sizer></Page>
    </Row>
  </div>
</template>

<script>

import {listTask, deleteTask, downloadTask,getCat2} from '@/api/task.js'
import { setCat2List,getCat2List } from '@/libs/util.js'
import {listUser} from "@/api/user"
import {formatDate} from '@/libs/tools.js'

export default {
  name: "taskList",
  activated() {
    this.pageListTask()
  },
  created() {
    this.queryCat2()
    this.listUserLookup()
  },
  mounted() {
    if (getCat2List()){
      this.cat2List=JSON.parse(getCat2List())
    }else {
      this.queryCat2()
    }
  },
  data () {
    return {
      param: {
        taskId: null,
        taskName: '',
        taskCat1: null,
        taskCat2: null,
        taskLevel: null,
        taskCreateTimeStart: null,
        taskCreateTimeEnd: null,
        taskEndDateStart: null,
        taskEndDateEnd: null,
        taskLeader: null,
        taskStatus: null,
        taskReason: null,
        taskMethod: null,
        taskRemarks: null,
        pageNum: 1,
        pageSize: 10
      },
      userList:[],
      cat2List: [],
      taskEndDate:[],
      taskCreateTime:[],
      taskIds:[],
      selectTaskIds:[],
      data: [],
      total: 0,
      isLoading: false,
      columns1: [
        {
          type: 'selection',
          width: 35,
          align: 'center'
        },{
          type: 'index',
          title: '序号',
          width: 80,
          align: 'center'
        },
        {
          title: '任务名称',
          key: 'taskName',
          tooltip: true
        },
        {
          title: '负责人',
          key: 'taskLeader'
        },
        {
          title: '紧急程度',
          key: 'taskLevel'
        },
        {
          title: '预计完成时间',
          key: 'taskEndDate'
        },
        {
          title: '创建时间',
          key: 'taskCreateTime'
        },
        {
          title: '任务状态',
          key: 'taskStatus',
          render: (h, params) => {
            const row = params.row;
            const color = row.taskStatus === '未完成' ? 'error' : row.taskStatus === '审核中' ? 'warning' : row.taskStatus === '已完成' ?'success':'';
            const text = row.taskStatus;

            return h('Tag', {
              props: {
                // type: 'dot',
                color: color
              }
            }, text);
          }
        },
        {
          title: '类别',
          key: 'taskCat1'
        },
        {
          title: '所属分类',
          key: 'taskCat2'
        },
        {
          title: '操作',
          key: 'action',
          width: 150,
          align: 'center',
          render: (h, action) => {
            return h('div', [
              h('Button', {
                props: {
                  type: 'primary',
                  size: 'small'
                },
                style: {
                  marginRight: '5px'
                },
                on: {
                  click: () => {
                    this.show(action.row.taskId)
                  }
                }
              }, '详情'),
              h('Button', {
                props: {
                  type: 'error',
                  size: 'small'
                },
                on: {
                  click: () => {
                    this.deleteTask(action.row.taskId,action.index)
                  }
                }
              }, '删除')
            ]);
          }
        }
      ]
    }
  },
  methods: {
    pageListTask(){
      this.isLoading=true
      if(this.param.pageNum===undefined||this.param.pageNum===null||this.param.pageNum==='')this.param.pageNum=1
      listTask(this.param).then(res=>{
        if(res.data.code===0){
          let list = res.data.data
          this.data=list.records
          this.total=list.total
          this.isLoading=false
        }else {
          this.$Message.error('网络出错，请联系管理员！')
        }
      })
    },
    downloadTask(){
      if (!this.taskIds.length>0) {
        this.$Message.warning('请选择要下载的任务！')
        return
      }
      downloadTask(this.taskIds).then(res=>{
        // console.info(res.response)
        // console.info(res.data.response)
        // const fileName = decodeURI(res.headers['Content-disposition'].split(';')[1].split('=')[1])
        //下载文件
        let blob = new Blob([res.data], {type: "application/vnd.ms-excel"})
        let objectUrl = URL.createObjectURL(blob) // 创建URL
        const link = document.createElement('a')
        link.href = objectUrl
        // link.download = '.xlsx'// 自定义文件名
        //这里是获取后台传过来的文件名
        link.setAttribute("download",formatDate(new Date())+'问题汇总.rar')
        link.click() // 下载文件
        URL.revokeObjectURL(objectUrl)
          // this.$Message.success('下载成功')
      })
    },
    changePageSize(e){
      this.param.pageSize=e
      this.pageListTask()
    },
    show(id){
      if (id!==null&&id!=='') {
        this.$router.push({name:'editTask',params:{id:id}})
      }
    },
    add(){
      this.$router.push({path:'addTask'})
    },
    deleteTask(id,index){
      if (!this.taskIds.includes(id)) this.$refs.selection.toggleSelect(index)
      let contentText='确定要删除'+this.taskIds.length+'条任务？'
      this.$Modal.confirm({
        title: 'Title',
        content: contentText,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          deleteTask(this.taskIds).then(res=>{
            if (res.data.code===0&&res.data.msg==='success'){
              this.$Message.info('删除成功！');
              this.pageListTask();
            }else {
              this.$Message.info('删除失败，请联系管理员！');
            }
          })
        }
      });
    },
    changePageNum(e){
      this.param.pageNum=e
      this.pageListTask()
    },
    handleSelectAll (status) {
      this.$refs.selection.selectAll(status);
    },
    changeCreateTime(e){
      this.param.taskCreateTimeStart=e[0]
      this.param.taskCreateTimeEnd=e[1]
    },
    changeEndTime(e){
      this.param.taskEndDateStart=e[0]
      this.param.taskEndDateEnd=e[1]
    },
    selectTask(e){
      this.taskIds=[]
      if (e.length>0){
        e.forEach((v, i)=>{
          this.taskIds.push(v.taskId)
        })
      }
    },
    dblclickTable(e,i){
      this.$refs.selection.toggleSelect(i)
    },
    queryCat2(){
      getCat2().then(res=>{
        if(res.data.code===0){
          this.cat2List=res.data.data
          setCat2List(JSON.stringify(this.cat2List))
        }else {
          this.$Message.error('网络出错，请联系管理员！')
        }
      })
    },
    listUserLookup(){
      listUser().then(res=>{
        let data=res.data
        if (data.code===0){
          this.userList=data.data
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
