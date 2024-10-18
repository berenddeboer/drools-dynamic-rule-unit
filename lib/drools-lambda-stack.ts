import * as path from "path"
import * as cdk from "aws-cdk-lib"
import { Construct } from "constructs"
import * as lambda from "aws-cdk-lib/aws-lambda"

export class DroolsLambdaStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props)

    // Define the path to the Lambda package (either JAR or native binary)
    const lambdaAssetPath = path.join(
      __dirname,
      "../java/build/libs/lambda-0.1-lambda.zip"
    )

    // Create the Lambda function
    const javaLambdaFunction = new lambda.Function(
      this,
      "MicronautDroolsLambdaFunction",
      {
        functionName: "micronaut-drools",
        runtime: lambda.Runtime.PROVIDED_AL2,
        handler: "bootstrap", // Change this to your Lambda handler class name
        code: lambda.Code.fromAsset(lambdaAssetPath),
        memorySize: 1024, // Adjust memory size as needed
        timeout: cdk.Duration.seconds(15), // Adjust the timeout as needed
      }
    )
  }
}
