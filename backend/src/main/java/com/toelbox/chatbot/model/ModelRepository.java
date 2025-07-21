package com.toelbox.chatbot.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface ModelRepository extends ListCrudRepository<Model, UUID>, PagingAndSortingRepository<Model, UUID> {
	List<Model> findAllByActive(boolean active, Sort sort);
}
