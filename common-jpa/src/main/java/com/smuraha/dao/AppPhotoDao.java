package com.smuraha.dao;

import com.smuraha.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoDao extends JpaRepository<AppPhoto,Long> {
}
