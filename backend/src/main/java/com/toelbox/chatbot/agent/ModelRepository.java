package com.toelbox.chatbot.agent;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ModelRepository extends CrudRepository<Model, UUID> {
}
