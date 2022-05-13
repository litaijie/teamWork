import axios from '@/libs/api.request'
import {formatDate} from '@/libs/tools.js'

export const save = (data) => {
  return axios.request({
    url: '/task/save',
    method: 'post',
    data: {
      taskId: data.taskId,
      taskName: data.taskName,
      taskCat1: data.taskCat1,
      taskCat2: data.taskCat2,
      taskLevel: data.taskLevel,
      taskEndDate: formatDate(data.taskEndDate),
      taskReason: data.taskReason,
      taskMethod: data.taskMethod,
      taskRemarks: data.taskRemarks,
      taskLeaderId: data.taskLeaderId,
      taskStatus:data.taskStatus,
      imageId: data.imageId,
      fileType: data.fileType,
      delImageId: data.delImageId
    }
  })
}

export const listTask = (data) => {
  return axios.request({
    url: '/task/listTask',
    method: 'post',
    data: {
      taskName: data.taskName,
      taskCat1: data.taskCat1,
      taskCat2: data.taskCat2,
      taskLevel: data.taskLevel,
      taskCreateTimeStart: formatDate(data.taskCreateTimeStart),
      taskCreateTimeEnd: formatDate(data.taskCreateTimeEnd),
      taskEndDateStart: formatDate(data.taskEndDateStart),
      taskEndDateEnd: formatDate(data.taskEndDateEnd),
      taskLeader: data.taskLeader,
      taskReason: data.taskReason,
      taskMethod: data.taskMethod,
      taskRemarks: data.taskRemarks,
      taskStatus: data.taskStatus,
      pageNum: data.pageNum,
      pageSize: data.pageSize
    }
  })
}

export const listTaskDayReport = (data) => {
  return axios.request({
    url: '/task/listTaskDayReport',
    method: 'post',
    data: {
      taskCreateTimeStart: formatDate(data.taskCreateTimeStart),
      taskLeader: data.taskLeader
    }
  })
}

export const saveTaskReport = (data) => {
  return axios.request({
    url: '/task/saveTaskReport',
    method: 'post',
    data: data
  })
}

export const readTask = (taskId) => {
  return axios.request({
    url: '/task/readTask',
    method: 'get',
    params: {
      taskId: taskId
    }
  })
}

export const downloadTask = (taskIds) => {
  return axios.request({
    url: '/task/downloadTask',
    method: 'post',
    data: taskIds,
    responseType:'arraybuffer',
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
    }
  })
}

export const deleteTask = (taskIds) => {
  return axios.request({
    url: '/task/delete',
    method: 'post',
    data: taskIds
  })
}

export const getCat2 = () => {
  return axios.request({
    url: '/task/getCat2',
    method: 'get'
  })
}
