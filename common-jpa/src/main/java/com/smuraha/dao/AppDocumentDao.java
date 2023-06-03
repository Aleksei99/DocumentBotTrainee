package com.smuraha.dao;

import com.smuraha.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentDao extends JpaRepository<AppDocument,Long> {
}
