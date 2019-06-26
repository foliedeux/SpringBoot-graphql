package io.springboot.topics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

	@Autowired
	private TopicRepository topicRepository;

	public List<Topics> getAllTopics() {
		List<Topics> topics = new ArrayList<>();
		topicRepository.findAll().forEach(topics::add);
		return topics;
	}
	
	public Topics getTopic(String id) {
		return topicRepository.findById(id).get();
	}

	public void addTopic(Topics topic) {
		topicRepository.save(topic);
	}

	public void updateTopic(Topics topic, String id) {
		topicRepository.save(topic);
	}

	public void deleteTopic(String id) {
		topicRepository.deleteById(id);
	}
	
}
