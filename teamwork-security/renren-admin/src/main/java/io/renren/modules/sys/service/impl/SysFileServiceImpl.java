/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.sys.dao.SysDictDao;
import io.renren.modules.sys.dao.SysFileDao;
import io.renren.modules.sys.entity.SysDictEntity;
import io.renren.modules.sys.entity.SysFile;
import io.renren.modules.sys.service.SysFileService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

@Service("sysFileService")
public class SysFileServiceImpl extends ServiceImpl<SysFileDao, SysFile> implements SysFileService {
	@Autowired
	private SysFileDao sysFileDao;

	@Autowired
	private SysDictDao sysDictDao;

	@Value("${file-path-prefix}")
	private String filePathPrefix;

	/**
	 * 保存文件信息
	 * @param sysFile
	 * @return
	 */
	@Override
	public boolean saveFile(SysFile sysFile){
		if (sysFile==null) return false;


		boolean saveResult = this.saveOrUpdate(sysFile);
		return saveResult;
	}

	@Override
	public String createFilePath(){
		QueryWrapper<SysDictEntity> dicParam =new QueryWrapper<>();
		dicParam.eq("code",filePathPrefix);
		SysDictEntity sysDictEntity = sysDictDao.selectOne(dicParam);
		if (sysDictEntity==null) return null;

		DateTime currentDay = DateTime.now();
		StringBuffer path =new StringBuffer(sysDictEntity.getValue());
		path.append(currentDay.toString("yyyy"))
				.append(File.separator);
		File file1=new File(path.toString());
		if (!file1.exists()) file1.mkdir();
		path.append(currentDay.toString("yyyyMM")).append(File.separator);
		File file =new File(path.toString());
		if (!file.exists()) file.mkdir();
		path.append(currentDay.toString("yyyyMMddhhmmss"))
				.append("-");
		return path.toString();
	}

	@Override
	public BufferedImage getFile(String fileName){
		QueryWrapper<SysFile> param =new QueryWrapper<>();
		param.eq("file_name",fileName);
		SysFile sysFile = sysFileDao.selectOne(param);
		if (sysFile==null){
			return null;
		}
		File f=new File(sysFile.getFilePath());
        BufferedImage image= null;
        try {
            image = (BufferedImage) ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
	}

}
