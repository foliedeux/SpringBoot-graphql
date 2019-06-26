package io.springboot.topics;


import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;


@RestController
public class TopicsController {
	
	@Autowired
	private TopicService topicservice;
	
	@Value("classpath:schema.graphqls")
	private Resource schemaResource;
	
	private GraphQL graphQL;
	
	@PostConstruct
	public void init()throws IOException {
//		URL url = Resources.getResource("schema.graphqls");
//        String sdl = Resources.toString(url, Charsets.UTF_8);
//        TypeDefinitionRegistry registry = new SchemaParser().parse(sdl);
//        RuntimeWiring wiring = buildWiring();
//        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
//        this.graphQL = GraphQL.newGraphQL(schema).build();
		File schemaFile = schemaResource.getFile();
		TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
		RuntimeWiring wiring = buildWiring();
		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
		graphQL = GraphQL.newGraphQL(schema).build();
	}

	private RuntimeWiring buildWiring() {
		DataFetcher<List<Topics>> fetcher1 = data -> {
			return (List<Topics>) topicservice.getAllTopics();
		};
		DataFetcher<Topics> fetcher2 = data -> {
			return topicservice.getTopic(data.getArgument("id"));
		};
		
		return RuntimeWiring.newRuntimeWiring().type("Query",
				typeWiring -> typeWiring.dataFetcher("getAllTopics", fetcher1)
				.dataFetcher("findTopic", fetcher2)).build();
	}

	@RequestMapping("/topics")
	public List<Topics> getAllTopics() {
		return topicservice.getAllTopics();
	}
	
	@RequestMapping("/topics/{id}")
	public Topics getTopic(@PathVariable String id) {
		return topicservice.getTopic(id);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/topics")
	public void addTopic(@RequestBody Topics topic) {
		topicservice.addTopic(topic);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/topics/{id}")
	public void updateTopic(@RequestBody Topics topic, @PathVariable String id) {
		topicservice.updateTopic(topic, id);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/topics/{id}")
	public void updateTopic(@PathVariable String id) {
		topicservice.deleteTopic(id);
	}
	
	@PostMapping("/getAll")
	public ResponseEntity<Object> getAll(@RequestBody String query){
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
	
	@PostMapping("/getById")
	public ResponseEntity<Object> getById(@RequestBody String query){
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
}
