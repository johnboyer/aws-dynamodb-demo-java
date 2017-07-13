# AWS DynamoDB Demo

## Overview
This project creates a sample `customer` table in AWS DynamoDB, adds sample items to it, and then queries them. This project is companion to my blog post: [How to Build a Serverless API With AWS Dynamodb, Lambda, and API Gateway](https://jeboyer.wordpress.com/2017/07/13/how-to-build-a-serverless-api-with-aws-dynamodb-lambda-and-api-gateway/).

## Create DynamoDB Table and Populate It
1. Clone the project: `$ git clone https://github.com/johnboyer/aws-dynamodb-demo-java.git`
2. Review the code that creates the table and populates it with sample data:

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
                
        private static void addSampleItems() {
            // Add an item
            Map<String, AttributeValue> item = createItem("john@example.com", "John", "Doe");
            PutItemRequest putItemRequest = new PutItemRequest(TABLE, item);
            PutItemResult putItemResult = sDynamoDB.putItem(putItemRequest);
            //...
        }
        
        
3. From the `aws-dynamodb-demo-java/dynamo-db` directory, package the project: 
`$ mvn package`
4. Then run the app: 
`$ mvn exec:java`

*For information about programming in DynamoDB click [here](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.html).*
