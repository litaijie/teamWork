<template>
  <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80">
    <Form-item label="bean名称" prop="beanName">
      <Select v-model="formValidate.beanName" placeholder="请选择bean">
        <Option value="createTask">报表任务</Option>
      </Select>
<!--      <Input v-model="formValidate.beanName" placeholder="请输入bean名称"></Input>-->
    </Form-item>
    <Form-item label="生成时点" prop="cronExpression">
      <Select v-model="formValidate.cronExpression">
        <Option value="0 0 2 1 * ?">每月1号</Option>
        <Option value="0 0 2 1 1,4,7,10 ?">每季度第1个月1号</Option>
      </Select>
    </Form-item>
    <Form-item label="任务名称" prop="taskName">
      <Input v-model="formValidate.taskName" placeholder="请输入任务名称"></Input>
    </Form-item>
    <Form-item label="所属类别" prop="taskCat1">
      <Radio-group v-model="formValidate.taskCat1">
        <Radio label="1">软件</Radio>
        <Radio label="2">硬件</Radio>
      </Radio-group>
    </Form-item>
    <Form-item label="所属分类" prop="taskCat2">
      <Select v-model="formValidate.taskCat2" placeholder="请选择所属分类">
        <Option value="1">服务器报错</Option>
        <Option value="2">操作系统</Option>
        <Option value="3">用户操作错误</Option>
        <Option value="4">程序变更</Option>
        <Option value="5">网络线路</Option>
        <Option value="6">打印机</Option>
        <Option value="7">键鼠周边</Option>
        <Option value="8">监控自助</Option>
        <Option value="9">其他</Option>
      </Select>
    </Form-item>
    <Form-item label="紧急程度" prop="taskLevel">
      <Select v-model="formValidate.taskLevel" placeholder="紧急程度">
        <Option value="1">一般</Option>
        <Option value="2">急</Option>
        <Option value="3">加急</Option>
      </Select>
    </Form-item>
    <Form-item label="完成日期" prop="taskEndDate">
      <Row>
        <DatePicker v-model="formValidate.taskEndDate" format="dd HH:mm:ss" type="datetime" placeholder="选择时间"></DatePicker>
      </Row>
    </Form-item>
    <Form-item label="问题原因" prop="taskReason">
      <Input v-model="formValidate.taskReason" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>

    <Form-item label="解决方案" prop="taskMethod">
      <Input v-model="formValidate.taskMethod" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>
    <Form-item label="备注" prop="taskRemarks">
      <Input v-model="formValidate.taskRemarks" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>
    <Form-item label="任务负责人" prop="taskLeader">
      <Select v-model="formValidate.taskLeaderId" placeholder="请选择任务负责人">
        <Option v-for="item in userList" :value="item.userId" :key="item.userId">{{ item.username }}</Option>
      </Select>
    </Form-item>
    <Form-item>
      <Button type="primary" @click="handleSubmit('formValidate')">提交</Button>
      <Button type="ghost" @click="handleReset('formValidate')" style="margin-left: 8px;color: darkred">重置</Button>
    </Form-item>
  </Form>
</template>
<script>
import {hasOneOf,formatDate} from '@/libs/tools'
import store from '@/store'
import {save} from '@/api/job.js'
import {listUser} from '@/api/user.js'
import {mapMutations} from "vuex"

export default {
  name: "taskJob",
  mounted () {
    this.listUserLookup()
    // this.checkTaskLeader = hasOneOf(store.state.user.access,['sys:user:checkLeader'])
    // this.formValidate.taskId=this.$route.query.taskId
    // this.taskDetail()
  },
  data () {
    return {
      formValidate: {
        taskId: null,
        taskName: '',
        taskCat1: null,
        taskCat2: null,
        taskLevel: null,
        taskEndDate: null,
        taskReason: '',
        taskMethod: '',
        taskRemarks: '',
        taskLeaderId: null,
        taskStatus:0,
        beanName: null,
        cronExpression: null
      },
      job:{
        beanName: null,
        cronExpression: null,
        params: null,
        status: null,
        remark: null,
        createTime: null
      },
      userList: [],
      checkTaskLeader: false,
      ruleValidate: {
        taskName: [
          { required: true, message: '任务名称不能为空', trigger: 'change' }
        ],
        taskCat2: [
          { required: true, message: '请选择所属分类', trigger: 'change' }
        ],
        taskCat1: [
          { required: true, message: '请选择所属类别', trigger: 'change' }
        ],
        taskLevel: [
          { required: true, message: '紧急程度', trigger: 'change' }
        ],
        taskEndDate: [
          { required: true, type: 'date', message: '请选择日期', trigger: 'change' }
        ],
        taskReason: [
          { required: true, message: '请输入问题原因', trigger: 'blur' },
          { type: 'string', min: 5, message: '不能少于5个字', trigger: 'blur' }
        ],
        beanName: [
          { required: true, message: 'bean名称不能为空', trigger: 'blur' }
        ],
        cronExpression: [
          { required: true, message: '请选择时钟刻度', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    ...mapMutations([
      'closeTag'
    ]),
    handleSubmit (name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.job.beanName=this.formValidate.beanName
          this.job.cronExpression=this.formValidate.cronExpression
          this.formValidate.taskEndDate=formatDate(this.formValidate.taskEndDate)
          this.job.params=JSON.stringify(this.formValidate)
         save(this.job).then(re=> {
           let data = re.data
           if (data.code === 0) {
             this.$Message.success('提交成功!');
             setTimeout(e=>{
               this.closeTag({name:'taskJob'})
             },1000)
           }else {
             this.$Message.error(data.msg);
           }
         })
        } else {
          this.$Message.error('表单验证失败!');
        }
      })
    },
    handleReset (name) {
      this.$refs[name].resetFields();
    },
    taskDetail(){
      let that = this
      let id = that.formValidate.taskId
      if (id!==null&&id!=='') {
        readTask(id).then(res => {
          let taskDetail = res.data.data
          if (taskDetail!==null&&taskDetail!=='') {
            that.formValidate.taskName=taskDetail.taskName
            that.formValidate.taskCat1=taskDetail.taskCat1.toString()
            that.formValidate.taskCat2=taskDetail.taskCat2.toString()
            that.formValidate.taskLevel=taskDetail.taskLevel.toString()
            that.formValidate.taskEndDate=taskDetail.taskEndDate
            that.formValidate.taskReason=taskDetail.taskReason
            that.formValidate.taskMethod=taskDetail.taskMethod
            that.formValidate.taskRemarks=taskDetail.taskRemarks
            that.formValidate.taskLeaderId=taskDetail.taskLeaderId
            that.formValidate.taskStatus=taskDetail.taskStatus.toString()
          }
        })
      }
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
