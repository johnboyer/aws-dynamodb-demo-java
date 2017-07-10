package me.johnboyer.samples;
import static com.amazonaws.services.dynamodbv2.model.ScalarAttributeType.S;

import java.util.Date;
/*
 * Copyright John Boyer. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.util.DateUtils;

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class AmazonDynamoDBSample {

	/**
	 * Email attribute, partition key
	 */
    private static final String EMAIL = "email";
    /**
     * First name attribute
     */
	private static final String FIRST_NAME = "first_name";
	/**
	 * Last name attribute
	 */
	private static final String LAST_NAME = "last_name";
	/**
	 * DynamoDB client
	 */
	private static AmazonDynamoDB sDynamoDB;
	/**
	 * Table name
	 */
    private static final String TABLE = "customer";
    /**
     * Test mode flag, change to <code>false</code> to stop deleting the table.
     */
    private static final boolean TEST_MODE = true;
    

    /**
     * Adds all the sample items to the table
     */
    private static void addSampleItems() {
		// Add an item
		Map<String, AttributeValue> item = createItem("john@example.com", "John", "Boyer");
		PutItemRequest putItemRequest = new PutItemRequest(TABLE, item);
		PutItemResult putItemResult = sDynamoDB.putItem(putItemRequest);
		System.out.println("Result: " + putItemResult);
		
		// Add an item
		item = createItem("jane@example.com", "Jane", "Doe");
		putItemRequest = new PutItemRequest(TABLE, item);
		putItemResult = sDynamoDB.putItem(putItemRequest);
		System.out.println("Result: " + putItemResult);
		
		// Add an item
		item = createItem("mary@example.com", "Mary", "Smith");
		putItemRequest = new PutItemRequest(TABLE, item);
		putItemResult = sDynamoDB.putItem(putItemRequest);
		System.out.println("Result: " + putItemResult);
		
		// Add an item
		item = createItem("bob@example.com", "Bob", "Smith");
		putItemRequest = new PutItemRequest(TABLE, item);
		putItemResult = sDynamoDB.putItem(putItemRequest);
		System.out.println("Result: " + putItemResult);
	}

    /**
     * Creates an item entry
     * @param email
     * @param firstName
     * @param lastName
     * @return A map of the entry
     */
    private static Map<String, AttributeValue> createItem(String email, String firstName, String lastName) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(EMAIL, new AttributeValue(email));
        item.put(FIRST_NAME, new AttributeValue(firstName));
        item.put(LAST_NAME, new AttributeValue(lastName));
		item.put("created_date", new AttributeValue(DateUtils.formatISO8601Date(new Date())));
        return item;
    }

    /**
     * Creates the table waits until it's active
     * @throws InterruptedException
     */
	private static void createTable() throws InterruptedException {
		AttributeDefinition[] defs = {
				                      new AttributeDefinition(EMAIL, S)
				                      };
		
		ProvisionedThroughput throughput = new ProvisionedThroughput()
				                                  .withReadCapacityUnits(1L)
				                                  .withWriteCapacityUnits(1L);
		
		//Email address is the key
		KeySchemaElement emailKey = new KeySchemaElement(EMAIL, KeyType.HASH);
		CreateTableRequest createTableRequest = new CreateTableRequest()
				                 .withTableName(TABLE)
		                         .withKeySchema(emailKey)
		                         .withAttributeDefinitions(defs)
		                         .withProvisionedThroughput(throughput);

		// Create table if it does not exist yet
		TableUtils.createTableIfNotExists(sDynamoDB, createTableRequest);
		// wait for the table to move into ACTIVE state
		TableUtils.waitUntilActive(sDynamoDB, TABLE);
	}

	/**
	 * Deletes the table unless <code>TEST_MODE</code> is <code>false</code>.
	 */
	private static void deleteTable() {
		if(TEST_MODE) {
			System.out.println("WARNING: Deleting the table and all its data. Set TEST_MODE to false to keep the table.");
			sDynamoDB.deleteTable(new DeleteTableRequest(TABLE));
		}
	}

	/**
	 * Describes the table
	 */
	private static void describeTable() {
		// Describe our new table
		DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(TABLE);
		TableDescription tableDescription = sDynamoDB.describeTable(describeTableRequest).getTable();
		System.out.println("Table Description: " + tableDescription);
	}

	/**
	 * Initializes the client
	 * @throws Exception
	 */
	private static void init() throws Exception {
        sDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    }

	/**
	 * Main method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
        init();

        try {

            createTable();

            describeTable();

            addSampleItems();

            scanTable();
            
            deleteTable();

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

	/**
	 * Scans the table for <i>John</i>
	 */
    private static void scanTable() {
		// Scan items for movies with a year attribute greater than 1985
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		Condition condition = new Condition()
		                           .withComparisonOperator(ComparisonOperator.CONTAINS)
		                           .withAttributeValueList(new AttributeValue().withS("John"));
		scanFilter.put(FIRST_NAME, condition);
		ScanRequest scanRequest = new ScanRequest(TABLE).withScanFilter(scanFilter);
		ScanResult scanResult = sDynamoDB.scan(scanRequest);
		System.out.println("Result: " + scanResult);
	}

}
