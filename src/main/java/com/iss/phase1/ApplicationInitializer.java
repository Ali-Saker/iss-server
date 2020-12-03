package com.iss.phase1;

import com.iss.phase1.client.entity.Document;
import com.iss.phase1.client.tcp.TCPServer;
import com.iss.phase1.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ApplicationInitializer implements CommandLineRunner {

    @Autowired
    private TCPServer tcpServer;

    @Autowired
    private DocumentRepository documentRepository;

    public ApplicationInitializer() {

    }

    @Override
    public void run(String... args) throws IOException {
        this.dbSeed();
        this.tcpServer.run(9999);
    }


    private void dbSeed() {
        if(documentRepository.count() == 0) {
            List<Document> documentList = Arrays.asList(
                    new Document("Document 1", "This ingenious tale examines the ambitions and petty jealousies of the staff at Critchley's Bank. From the doorman to the personnel manager, to Sir William, the bank's sorrowful chairman. Archer knits a panoply of characters with deft narrative skill in a story which is as revealing as it is observant. Taken from his anthology To Cut a Long Story Short, his other short story anthologies include A Quiver Full of Arrows, Twelve Red Herrings and A Twist in the Tale."),
                    new Document("Document 2", "Two men meet in a seaside resort. What follows is murder. What's discovered isn't all it seems to be... Arnold Bennett was born in 1867. He wrote an influential book column for the Evening Standard from 1926 until his death in London in 1931. He was a great admirer of the French realists and his most successful novels, Clayhanger, Hilda Lessways, and These Twain, set in his native Staffordshire, show the literary influence of Flaubert and Balzac on his work. He also wrote plays and essays, as well as many short stories."),
                    new Document("Document 3", "John Bidwell's two satirical short story journals are loosely based on real incidents in the author's life. A Boy at Seven is set in a Jesuit boarding school in the early 1960s where some of the boys, fed up with their hateful and sadistic Prefect of Discipline, Father John Kilbracken, hatch a plan to humiliate him in front of all. In Fear and Loathing in Aspen, the author observes the strange behaviour of Americans at the famous Colorado skiing resort of Aspen and follows one of them, a hairy billionaire called Abe Fineblown, on a gung-ho shooting mission to Kenya."),
                    new Document("Document 4", "Alfred Edgar Coppard, the son of a Kentish journeyman tailor and a hosteler's daughter, wrote his first short story at the age of 43 and achieved fame in his lifetime, for his vivid depictions of the English countryside and its rural characters. The Higgler, which first appeared in his anthology Fishmonger's Fiddle in 1925, is one of his finest works; a strangely unpredictable tale of an itinerant dealer in poultry and eggs whose emotional involvement with the mother and daughter of an isolated farmhouse on the moors threatens to become an obsession.")
            );
            documentRepository.saveAll(documentList);
        }
    }
}
