if [ $# -eq 0 ];
then
    >&2 echo "No arguments provided"
    APP_MAIN="spark.SparkSessionExample"
else
  APP_MAIN="spark.$1"
fi

JAR_DIRECTORY="/app/scala-2.12"
JAR_NAME="spark-processing_2.12-0.1.jar"

echo "Running:  $JAR_DIRECTORY/$APP_MAIN"

/spark/bin/spark-submit --class "$APP_MAIN" --packages "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.1" --master local[*] $JAR_DIRECTORY/$JAR_NAME
