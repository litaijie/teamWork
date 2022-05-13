<template>
  <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80">
    <Form-item label="任务名称" prop="taskName">
      <Input v-model="formValidate.taskName" :disabled="this.isDisable" placeholder="请输入任务名称"></Input>
    </Form-item>
    <Form-item label="所属类别" prop="taskCat1">
      <Radio-group v-model="formValidate.taskCat1.toString()">
        <Radio label="1" :disabled="this.isDisable">软件</Radio>
        <Radio label="2" :disabled="this.isDisable">硬件</Radio>
      </Radio-group>
    </Form-item>
    <Form-item label="所属分类" prop="taskCat2">
      <Select v-model="formValidate.taskCat2.toString()" placeholder="请选择所属分类" :key="this.$route.params.id" :disabled="this.isDisable">
        <Option v-for="item in this.cat2List" :key="item.code" :value="item.code">{{item.value}}</Option>
      </Select>
    </Form-item>
    <Form-item label="紧急程度" prop="taskLevel">
      <Select v-model="formValidate.taskLevel.toString()" placeholder="紧急程度" :disabled="this.isDisable">
        <Option value="1">一般</Option>
        <Option value="2">急</Option>
        <Option value="3">加急</Option>
      </Select>
    </Form-item>
    <Form-item label="完成日期" prop="taskEndDate">
      <Row>
        <DatePicker  :disabled="this.isDisable" v-model="formValidate.taskEndDate" format="yyyy-MM-dd HH:mm:ss" type="datetime" placeholder="选择时间"></DatePicker>
      </Row>
    </Form-item>
    <Form-item label="问题原因" prop="taskReason">
      <Input :disabled="this.isDisable" v-model="formValidate.taskReason" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>

    <Form-item label="解决方案" prop="taskMethod">
      <Input :disabled="this.isDisable" v-model="formValidate.taskMethod " type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>
    <Form-item label="图片" prop="image">
      <div class="demo-upload-list" v-for="item in uploadList">
        <template v-if="item.status === 'finished'">
          <img :src="item.url">
          <div class="demo-upload-list-cover">
            <Icon type="ios-eye-outline" @click.native="handleView(item.name)"></Icon>
            <Icon type="ios-trash-outline" @click.native="handleRemove(item)"></Icon>
          </div>
        </template>
        <template v-else>
          <Progress v-if="item.showProgress" :percent="item.percentage" hide-info></Progress>
        </template>
      </div>
      <Upload
        ref="upload"
        :show-upload-list="false"
        :default-file-list="defaultList"
        :on-success="handleSuccess"
        :format="['jpg','jpeg','png']"
        :max-size="2048"
        :on-format-error="handleFormatError"
        :on-exceeded-size="handleMaxSize"
        :before-upload="handleBeforeUpload"
        multiple
        type="drag"
        :action="uploadUrl"
        style="display: inline-block;width:58px;">
        <div style="width: 58px;height:58px;line-height: 58px;">
          <Icon type="ios-camera" size="20"></Icon>
        </div>
      </Upload>
      <Modal title="预览详情" v-model="visible" fullscreen>
        <img :src="viewImageUrl +  imgName " v-if="visible" style="width: auto">
      </Modal>
    </Form-item>
    <Form-item label="备注" prop="taskRemarks">
      <Input :disabled="this.isDisable" v-model="formValidate.taskRemarks" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>
    <Form-item label="任务负责人" prop="taskLeader" v-if="this.checkTaskLeader">
      <Select :disabled="this.isDisable" v-model="formValidate.taskLeaderId.toString()" placeholder="请选择任务负责人">
        <Option v-for="item in userList" :value="item.userId.toString()" :key="item.userId.toString()">{{ item.username }}</Option>
      </Select>
    </Form-item>
    <Form-item>
      <Button type="primary" @click="handleSubmit('formValidate','submit')" :style="{display: this.submitButHidden}">保存</Button>
      <Button type="warning" @click="handleSubmit('formValidate','check')" :style="{marginLeft: '8px',display: this.checkButHidden}">{{butName}}</Button>
      <Button type="success" @click="handleSubmit('formValidate','process')" :style="{marginLeft: '8px',display: this.butHidden}">{{this.processButName}}</Button>
      <Button type="info" @click="goBack" :style="{marginLeft: '8px'}">返回</Button>
<!--      <Button type="ghost" @click="handleReset('formValidate')" style="margin-left: 8px;color: darkred" :style="{marginLeft: '8px',color: 'darkred',display: this.submitButHidden}">重置</Button>-->
    </Form-item>
  </Form>
</template>
<script>
import {hasOneOf} from '@/libs/tools'
import store from '@/store'
import {save,readTask,getCat2} from '@/api/task.js'
import {mapMutations} from "vuex"
import {listUser} from "@/api/user"
import { setCat2List,getCat2List,getNewTagList } from '@/libs/util.js'
import config from '@/config'

export default {
  name: "editTask",
  watch:{
    $route :  {
      handler: function (to,from){
      // &&typeof(from.params.id)!='undefined'
        if (this.$route.params.id) {
          //获取详情内容
          this.butName= ''
          this.processButName= ''
          this.butHidden= 'none'
          this.submitButHidden= ''
          this.checkButHidden= ''
          this.isDisable= true
          this.taskDetail(to.params.id)
        }
      },
      immediate:true
    }
  },
  created() {
    //加载分类下拉框数据
    this.cat2List=JSON.parse(getCat2List())
  },
  mounted() {
    this.uploadList = this.$refs.upload.fileList
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
        taskStatus:null,
        imageId: [],
        delImageId: [],
        fileType: 'task_pic',
        taskFiles: []
      },
      cat2List:[],
      userList: [],
      checkTaskLeader: false,
      butName: '',
      processButName: '',
      butHidden: 'none',
      submitButHidden: '',
      checkButHidden: '',
      isDisable: true,
      ruleValidate: {
        taskName: [
          { required: true, message: '任务名称不能为空', trigger: 'blur' }
        ],
        taskCat2: [
          { required: true, message: '请选择所属分类', trigger: 'change',type:'number' }
        ],
        taskCat1: [
          { required: true, message: '请选择所属类别', trigger: 'change' ,type:'number'}
        ],
        taskLevel: [
          { required: true, message: '紧急程度', trigger: 'change' ,type:'number'}
        ],
        taskEndDate: [
          { required: true, type: 'date', message: '请选择日期', trigger: 'change' }
        ],
        taskReason: [
          { required: true, message: '请输入问题原因', trigger: 'blur' },
          { type: 'string', min: 5, message: '不能少于5个字', trigger: 'blur' }
        ],
        taskMethod:[
          { required: false, message: '发送审核必须填写解决方案，不少于5个字', trigger: 'blur' },
          { type: 'string', min: 5, message: '不能少于5个字', trigger: 'blur' }
        ]
      },
      defaultList: [],
      imgName: '',
      visible: false,
      uploadList: [],
      uploadUrl:config.baseUrl.pro+'sys/file/batchFileUpload',
      viewImageUrl: config.baseUrl.pro+'sys/file/image/'
    }
  },
  methods: {
    ...mapMutations([
      'closeTag',
      'setTagNavList'
    ]),
    initPage(){
      this.checkTaskLeader = hasOneOf(store.state.user.access,['sys:user:checkLeader'])
      if (this.checkTaskLeader){
        this.listUserLookup()
      }

      //获取详情内容
      if (this.formValidate.taskStatus!==null) {
        //判断用户权限
        if (this.checkTaskLeader) {
          //提交审核后，显示确认完成按钮
          if (this.formValidate.taskStatus > 0) this.butHidden = ''
        }
        //提交审核后，隐藏保存按钮；
        if (this.formValidate.taskStatus > 0) this.submitButHidden = 'none'
        //判断发送审核
        if (this.formValidate.taskStatus === 0) {
          if (store.state.user.userId !== this.formValidate.taskLeaderId) this.checkButHidden = 'none' //非任务单负责人，隐藏审核按钮
          this.butName = '发送审核'
          this.isDisable = false
        } else if (this.formValidate.taskStatus === 1) {
          this.butName = '取消审核'
          this.processButName = '确认完成'
        } else if (this.formValidate.taskStatus === 2) {
          this.processButName = '取消完成'
          //任务完成，隐藏审核按钮
          this.checkButHidden = 'none'
        } else {
          this.butName = ''
        }
      }
    },
    handleSubmit (name, taskStatus) {
      if (taskStatus==='check') {
        if (this.butName==='发送审核') {
          this.formValidate.taskStatus=1
          this.ruleValidate.taskMethod[0].required=true//设置解决方案必填
        }
        else if (this.butName==='取消审核') this.formValidate.taskStatus=0
      }else if (taskStatus==='process'){
        if (this.processButName==='确认完成') this.formValidate.taskStatus=2
        else if (this.processButName==='取消完成') this.formValidate.taskStatus=1
      }else {
        this.formValidate.taskStatus=0
        this.ruleValidate.taskMethod[0].required=false//设置解决方案非必填
      }

      this.$refs[name].validate((valid) => {
        if (valid) {
          if (this.formValidate.fileType===null||this.formValidate.fileType==='') this.formValidate.fileType='task_pic'
          save(this.formValidate).then(re=> {
            let data = re.data
            if (data.code === 0) {
              this.$Message.success('提交成功!')
              setTimeout(e=>{
                this.closeTag(this.$route)
                this.setTagNavList(this.$store.state.app.tagNavList)
              },500)
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
    taskDetail(id){
      readTask(id).then(res => {
        this.formValidate = res.data.data
        let files =res.data.data.taskFiles
        if (files&&files.length>0){
          files.forEach((i,v)=>{
            let file = {
              url: this.viewImageUrl+i.fileName+'.'+i.fileFormat,
              name: i.fileName+'.'+i.fileFormat,
              status: 'finished',
              isSaved: true,
              id: i.taskFileId
            }
            this.uploadList.push(file)
          })
        }
      }).then(e=>{
        this.initPage()
      })
    },
    listUserLookup(){
      listUser().then(res=>{
        let data=res.data
        if (data.code===0){
          this.userList=data.data
        }
      })
    },
    goBack(){
      this.$router.go(-1)
    },
    handleView (name) {
      this.imgName = name;
      this.visible = true;
    },
    handleRemove (file) {
      console.info( this.$refs.upload.fileList)
      let fileLists = null
      if (this.$refs.upload.fileList.length>0){
        // console.info('大于0')
        fileLists = this.$refs.upload.fileList
        this.$refs.upload.fileList.splice(fileLists.indexOf(file), 1)
      }else {
        // console.info('不大于0')
        fileLists = this.uploadList
        // console.info(fileLists)
        // console.info(file)
        this.uploadList.splice(fileLists.indexOf(file), 1)
        // console.info('uploadList')
        // console.info(this.uploadList)
        // console.info(this.formValidate)
        if (this.formValidate.delImageId===null) this.formValidate.delImageId=[]
        if (file.isSaved) this.formValidate.delImageId.push(file.id)
      }

      // console.info(this.formValidate.delImageId)
    },
    handleSuccess (res, file) {
      if (typeof(res.msg.success!=='undefined')) {
        file.name=res.msg.success.fileName+'.'+res.msg.success.fileFormat
        file.url=this.viewImageUrl+file.name
        if (this.formValidate.imageId===null) {this.formValidate.imageId=[]}
        this.formValidate.imageId.push(res.msg.success.id)
        this.uploadList.push(file)
      }
    },
    handleFormatError (file) {
      this.$Notice.warning({
        title: 'The file format is incorrect',
        desc: 'File format of ' + file.name + ' is incorrect, please select jpg or png.'
      });
    },
    handleMaxSize (file) {
      this.$Notice.warning({
        title: 'Exceeding file size limit',
        desc: 'File  ' + file.name + ' is too large, no more than 2M.'
      });
    },
    handleBeforeUpload () {
      const check = this.uploadList.length < 5;
      if (!check) {
        this.$Notice.warning({
          title: 'Up to five pictures can be uploaded.'
        });
      }
      return check;
    }
  }
}
</script>
<style>
.demo-upload-list{
  display: inline-block;
  width: 60px;
  height: 60px;
  text-align: center;
  line-height: 60px;
  border: 1px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
  position: relative;
  box-shadow: 0 1px 1px rgba(0,0,0,.2);
  margin-right: 4px;
}
.demo-upload-list img{
  width: 100%;
  height: 100%;
}
.demo-upload-list-cover{
  display: none;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0,0,0,.6);
}
.demo-upload-list:hover .demo-upload-list-cover{
  display: block;
}
.demo-upload-list-cover i{
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  margin: 0 2px;
}
</style>
