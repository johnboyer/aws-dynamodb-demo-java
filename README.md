# AWS DynamoDB Demo

## Overview
This project creates a sample `customer` table in AWS DynamoDB, adds sample items to it, and then queries them. This project is companion to my blog post: [How to Build a Serverless API With AWS Dynamodb, Lambda, and API Gateway](https://jeboyer.wordpress.com/2017/07/13/how-to-build-a-serverless-api-with-aws-dynamodb-lambda-and-api-gateway/).

## Prerequisites
1. An AWS Account and AWS CLI configured. Learn more [here](https://docs.aws.amazon.com/lambda/latest/dg/setup.html).
2. Git is installed and configured, click [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) for instructions.
3. Maven is installed and configured, click [here](http://maven.apache.org/install.html) for instructions.

## Create DynamoDB Table and Populate It
1. Ensure that Maven is [installed and configured](http://maven.apache.org/install.html) on your computer.
2. Clone the project: `$ git clone https://github.com/johnboyer/aws-dynamodb-demo-java.git`
3. Review the code that creates the table and populates it with sample data:

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
        
        
4. From the `aws-dynamodb-demo-java/dynamo-db` directory, package the project: 
`$ mvn package`
6. Then run the app: 
`$ mvn exec:java`

*For information about programming in DynamoDB click [here](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.html).*
