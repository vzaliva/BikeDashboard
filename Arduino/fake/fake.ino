
unsigned int counter = 0;

void setup() {
  Serial.begin(9600);
}

void loop() {
  delay(25);
  Serial.println(counter++);
  delay(25);
  Serial.println(counter++);
  delay(25);
  Serial.println(counter++);
  delay(25);
  Serial.println(counter++);
  delay(200);
}
