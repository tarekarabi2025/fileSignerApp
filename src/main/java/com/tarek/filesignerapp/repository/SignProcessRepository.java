package com.tarek.filesignerapp.repository;

import com.tarek.filesignerapp.model.SignProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignProcessRepository  extends JpaRepository<SignProcess, Long> {

}
