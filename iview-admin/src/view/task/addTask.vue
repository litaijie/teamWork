<template>
  <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80">
    <Form-item label="任务名称" prop="taskName">
      <Input v-model="formValidate.taskName" :disabled="this.isDisable" placeholder="请输入任务名称"></Input>
    </Form-item>
    <Form-item label="所属类别" prop="taskCat1">
      <Radio-group v-model="formValidate.taskCat1">
        <Radio label="1" :disabled="this.isDisable">软件</Radio>
        <Radio label="2" :disabled="this.isDisable">硬件</Radio>
      </Radio-group>
    </Form-item>
    <Form-item label="所属分类" prop="taskCat2">
      <Select v-model="formValidate.taskCat2" placeholder="请选择所属分类"  :disabled="this.isDisable">
        <Option v-for="item in this.cat2List" :key="item.code" :value="item.code">{{item.value}}</Option>
      </Select>
    </Form-item>
    <Form-item label="紧急程度" prop="taskLevel">
      <Select v-model="formValidate.taskLevel" placeholder="紧急程度" :disabled="this.isDisable">
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
        <img :src=" viewImageUrl + imgName " v-if="visible" style="width: 100%">
      </Modal>
    </Form-item>
    <Form-item label="备注" prop="taskRemarks">
      <Input :disabled="this.isDisable" v-model="formValidate.taskRemarks" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></Input>
    </Form-item>
    <Form-item label="任务负责人" prop="taskLeader" v-if="this.checkTaskLeader">
      <Select :disabled="this.isDisable" v-model="formValidate.taskLeaderId" placeholder="请选择任务负责人">
        <Option v-for="item in userList" :value="item.userId" :key="item.userId">{{ item.username }}</Option>
      </Select>
    </Form-item>
    <Form-item>
      <Button type="primary" @click="handleSubmit('formValidate')">保存</Button>
    </Form-item>
  </Form>
</template>
<script>
import {hasOneOf} from '@/libs/tools'
import store from '@/store'
import {save,readTask,getCat2} from '@/api/task.js'
import {mapMutations} from "vuex"
import {listUser} from "@/api/user"
import { setCat2List,getCat2List } from '@/libs/util.js'
import config from '@/config'

export default {
  name: "addTask",
  mounted () {
    this.uploadList = this.$refs.upload.fileList;
    //加载分类下拉框数据
    this.cat2List=JSON.parse(getCat2List())

    this.checkTaskLeader = hasOneOf(store.state.user.access,['sys:user:checkLeader'])
    if (this.checkTaskLeader){
      this.listUserLookup()
    }
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
        imageId: [],
        fileType: 'task_pic'
      },
      cat2List:[],
      userList: [],
      checkTaskLeader: false,
      isDisable: false,
      ruleValidate: {
        taskName: [
          { required: true, message: '任务名称不能为空', trigger: 'blur' }
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
      'closeTag'
    ]),
    handleSubmit (name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          if (this.formValidate.fileType===null||this.formValidate.fileType==='') this.formValidate.fileType='task_pic'
          save(this.formValidate).then(re=> {
            let data = re.data
            if (data.code === 0) {
              this.$Message.success('提交成功!');
              setTimeout(e=>{
                this.closeTag(this.$route)
                this.$router.replace({path:'taskList'})
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
    listUserLookup(){
      listUser().then(res=>{
        let data=res.data
        if (data.code===0){
          this.userList=data.data
        }
      })
    },
    handleView (name) {
      this.imgName = name;
      this.visible = true;
    },
    handleRemove (file) {
      const fileList = this.$refs.upload.fileList;
      this.$refs.upload.fileList.splice(fileList.indexOf(file), 1);
    },
    handleSuccess (res, file) {
      if (typeof(res.msg.success!=='undefined')) {
        file.name=res.msg.success.fileName+'.'+res.msg.success.fileFormat
        file.url=this.viewImageUrl+file.name
        this.formValidate.imageId.push(res.msg.success.id)
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
