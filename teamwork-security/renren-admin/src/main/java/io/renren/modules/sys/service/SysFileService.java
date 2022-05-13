/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.sys.entity.SysFile;

import java.awt.image.BufferedImage;

/**
 * 系统配置信息
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysFileService extends IService<SysFile> {

	boolean saveFile(SysFile sysFile);

	String createFilePath();

    BufferedImage getFile(String fileName);
}
