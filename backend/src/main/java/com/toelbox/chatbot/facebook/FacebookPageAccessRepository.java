package com.toelbox.chatbot.facebook;

import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

interface FacebookPageAccessRepository extends ListCrudRepository<FacebookPageAccess, UUID> {
}
