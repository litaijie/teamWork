import axios from "@/libs/api.request";
import {formatDate} from "@/libs/tools";

export const save = (data) => {
  return axios.request({
    url: '/sys/schedule/save',
    method: 'post',
    data: data
  })
}
