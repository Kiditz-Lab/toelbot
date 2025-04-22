package com.toelbox.chatbot.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ModelRepository extends CrudRepository<Model, UUID>, PagingAndSortingRepository<Model, UUID> {
}
