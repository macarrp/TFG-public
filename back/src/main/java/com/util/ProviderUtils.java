package es.aragon.espresbk.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import es.aragon.core.common.beans.PaginacionDto;
import es.aragon.core.common.beans.Paginated;
import es.aragon.espresbk.entity.sort.SortDto;
import es.aragon.espresbk.entity.sort.SortValue;
import es.aragon.espresbk.entity.sort.SortValueEnum;
import es.aragon.espresbk.entity.sort.SortValueFactory;
import es.aragon.espresbk.providers.PaginacionConSortProvider;

@Component
public class ProviderUtils {

    @Autowired
    PaginacionConSortProvider paginacionConSortProvider;
    
    @Autowired
    private ModelMapper modelMapper;

    public PaginacionDto getPagination(Page page) {
        PaginacionDto pagination = new PaginacionDto();
        pagination.setTotal(page.getTotalElements());
        pagination.setPaginas(page.getTotalPages());
        pagination.setItemPerPage(page.getNumberOfElements());
        return pagination;
    }


    public <ENTITY, DTO> Paginated<DTO> getPaginated(Page<ENTITY> page, Class<DTO> dto) {
        Paginated<DTO> paginated = new Paginated<>();

        paginated.setContent(mapList(page.getContent(), dto));
        paginated.setPaginacion(getPagination(page));
        return paginated;
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    	modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public <S, T> List<T> mapCollectionToList(Collection<S> source, Class<T> targetClass) {
    	modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public <S, T> T mapElement(S source, Class<T> targetClass) {
    	modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, targetClass);
    }


    public Pageable pagesToFind(int page, Integer itemsperpage) {
        return page == -1
                ? Pageable.unpaged()
                : PageRequest.of(page, itemsperpage);
    }


//    public List<Long> getIdListFromString(String idList) {
//        return Arrays.stream(idList.split(Constants.ID_SEPARATOR))
//                .map(Long::valueOf).collect(Collectors.toList());
//    }

    public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public <ENTITY, ID, DTO> Paginated<DTO> getAllPaginated(JpaRepository<ENTITY, ID> repository, int page, Integer itemsperpage, Class<DTO> dto) {
        Page<ENTITY> entityPage = repository.findAll(pagesToFind(page, itemsperpage));
        return getPaginated(entityPage, dto);
    }

    public Long calculateDateBetween(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null) fechaInicio = new Date();
        if (fechaFin == null) fechaFin = new Date();
        return ChronoUnit.DAYS.between(fechaInicio.toInstant(), fechaFin.toInstant());
    }

    public Pageable pagesToFindSorted(int page, Integer itemsperpage, SortValueEnum sortValueEnum, List<String> sortFields) {
        SortValue sortValues = SortValueFactory.getSortValues(sortValueEnum);
        List<SortDto> defaultValues = sortValues.getDefaultSortValues();
        List<String> fieldNames = sortValues.getEntityFieldNames();
        return paginacionConSortProvider.getPageableWithSort(page, itemsperpage, defaultValues,
                sortFields, fieldNames);
    }


	
	public byte[] joinDocumentos(List<byte[]> prints) throws DocumentException, IOException {
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		Document document = new Document();
//		PdfCopy copy = new PdfCopy(document, outputStream);
//		document.open();
//		for (byte[] print : prints) {
//			PdfReader reader = new PdfReader(print);
//			copy.addDocument(reader);
//			copy.freeReader(reader);
//			reader.close();
//		}
//		document.close();
//		return outputStream.toByteArray();
		
		//Create document and pdfReader objects.
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document();
		List<PdfReader> readers = new ArrayList<PdfReader>();
		int totalPages = 0;
		
		for (byte[] print : prints) {
			PdfReader reader = new PdfReader(print);
			readers.add(reader);
			totalPages = totalPages + reader.getNumberOfPages();
		}
		
		// Create writer for the outputStream
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		//Open document.
		document.open();
		//Contain the pdf data.
		PdfContentByte pageContentByte = writer.getDirectContent();
		
		PdfImportedPage pdfImportedPage;
		int currentPdfReaderPage = 1;
		Iterator<PdfReader> iteratorPDFReader = readers.iterator();
		
		// Iterate and process the reader list.
		while (iteratorPDFReader.hasNext()) {
			PdfReader pdfReader = iteratorPDFReader.next();
			//Create page and add content.
			while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
				document.newPage();
				pdfImportedPage = 
				writer.getImportedPage(pdfReader,currentPdfReaderPage);
				pageContentByte.addTemplate(pdfImportedPage, 0, 0);
				currentPdfReaderPage++;
			}
			currentPdfReaderPage = 1;
		}
		
		//Close document and outputStream.
		outputStream.flush();
		document.close();
		outputStream.close();
		
		return outputStream.toByteArray();		
	}
	
}
